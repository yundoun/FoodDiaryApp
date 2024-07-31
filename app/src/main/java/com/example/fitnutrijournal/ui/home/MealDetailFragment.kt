package com.example.fitnutrijournal.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.model.MealWithFood
import com.example.fitnutrijournal.databinding.FragmentMealDetailBinding
import com.example.fitnutrijournal.ui.diet.MealWithFoodAdapter
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.DietViewModelFactory
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.example.fitnutrijournal.viewmodel.PhotoViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class MealDetailFragment : Fragment() {

    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!
    private val photoViewModel: PhotoViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val dietViewModel: DietViewModel by activityViewModels {
        DietViewModelFactory(requireActivity().application, homeViewModel)
    }

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val REQUEST_IMAGE_CROP = 3
    private val REQUEST_PERMISSIONS = 1001
    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealDetailBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        (activity as MainActivity).showBottomNavigation(false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        checkPermissions()

        dietViewModel.setCheckboxVisible(null)

        val recyclerView = binding.foodList
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = MealWithFoodAdapter(
            emptyList(),
            { mealWithFood ->
                dietViewModel.selectFood(mealWithFood.food.foodCd)
                dietViewModel.setSaveButtonVisibility(false)
                dietViewModel.setUpdateButtonVisibility(true)
                findNavController().navigate(R.id.action_mealDetailFragment_to_foodDetailFragment)
            },
            dietViewModel
        )
        recyclerView.adapter = adapter

        setupItemTouchHelper(recyclerView, adapter)

        binding.btnAddFood.setOnClickListener {
            val source = homeViewModel.mealType.value ?: "breakfast"
            val action =
                MealDetailFragmentDirections.actionMealDetailFragmentToNavigationDiet(source)
            findNavController().navigate(action)
        }

        binding.cameraBtn.setOnClickListener {
            showImageSourceDialog()
        }

        homeViewModel.mealType.observe(viewLifecycleOwner) { mealType ->
            dietViewModel.setMealType(mealType)
            homeViewModel.filterFoodsByMealType(mealType)
            val mealText = when (mealType) {
                "breakfast" -> {
                    homeViewModel.currentCaloriesBreakfast.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = "$calories kcal\n총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeBreakfast.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = "$carb g\n탄수화물"
                    }
                    homeViewModel.currentProteinIntakeBreakfast.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = "$protein g\n단백질"
                    }
                    homeViewModel.currentFatIntakeBreakfast.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = "$fat g\n지방"
                    }
                    "아침"
                }

                "lunch" -> {
                    homeViewModel.currentCaloriesLunch.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = "$calories kcal\n총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeLunch.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = "$carb g\n탄수화물"
                    }
                    homeViewModel.currentProteinIntakeLunch.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = "$protein g\n단백질"
                    }
                    homeViewModel.currentFatIntakeLunch.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = "$fat g\n지방"
                    }
                    "점심"
                }

                "dinner" -> {
                    homeViewModel.currentCaloriesDinner.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = "$calories kcal\n총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeDinner.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = "$carb g\n탄수화물"
                    }
                    homeViewModel.currentProteinIntakeDinner.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = "$protein g\n단백질"
                    }
                    homeViewModel.currentFatIntakeDinner.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = "$fat g\n지방"
                    }
                    "저녁"
                }

                "snack" -> {
                    homeViewModel.currentCaloriesSnack.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = "$calories kcal\n총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeSnack.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = "$carb g\n탄수화물"
                    }
                    homeViewModel.currentProteinIntakeSnack.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = "$protein g\n단백질"
                    }
                    homeViewModel.currentFatIntakeSnack.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = "$fat g\n지방"
                    }
                    "간식"
                }

                else -> "식사"
            }
            binding.mealType.text = mealText
        }

        homeViewModel.filteredFoods.observe(viewLifecycleOwner) { foods ->
            viewLifecycleOwner.lifecycleScope.launch {
                val uniqueMeals = mutableListOf<MealWithFood>()
                val date = homeViewModel.currentDate.value ?: LocalDate.now()
                    .format(homeViewModel.dateFormatter)
                val mealType = homeViewModel.mealType.value ?: "breakfast"
                val meals = homeViewModel.mealRepository.getMealsByDateAndTypeSync(
                    date,
                    mealType
                ) // 필터링된 mealType으로 가져오기

                meals.forEach { meal ->
                    val food = foods.find { it.foodCd == meal.dietFoodCode }
                    if (food != null) {
                        uniqueMeals.add(MealWithFood(meal = meal, food = food))
                    }
                }

                Log.d("MealDetailFragment", "Updating Adapter with Meals: $uniqueMeals")
                adapter.updateMealsWithFood(uniqueMeals)
            }
        }




        dietViewModel.loadMealsWithFood()
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("카메라로 촬영", "앨범에서 선택")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("사진 추가")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> dispatchPickPictureIntent()
                }
            }
        builder.create().show()
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.fitnutrijournal.fileprovider", // 이 값이 AndroidManifest.xml과 일치해야 합니다.
                    it
                )
                photoUri = photoURI
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("camera", "onActivityResult: requestCode: $requestCode, resultCode: $resultCode, data: $data")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    Log.d("camera", "REQUEST_IMAGE_CAPTURE, photoUri: $photoUri")
                    photoUri?.let { cropImage(it) }
                }

                REQUEST_IMAGE_PICK -> {
                    Log.d("camera", "REQUEST_IMAGE_PICK, data: $data")
                    data?.data?.let { uri ->
                        currentPhotoPath = uri.toString()
                        cropImage(uri)
                    }
                }

                REQUEST_IMAGE_CROP -> {
                    Log.d("camera", "REQUEST_IMAGE_CROP, currentPhotoPath: $currentPhotoPath")
                    val date = homeViewModel.currentDate.value ?: return
                    val mealType = homeViewModel.mealType.value ?: return
                    currentPhotoPath?.let {
                        photoViewModel.addPhoto(date, mealType, it)
                        setPic(binding.imageSample, it)
                        Toast.makeText(requireContext(), "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    @SuppressLint("QueryPermissionsNeeded")
    private fun cropImage(uri: Uri) {
        try {
            val cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri, "image/*")
            cropIntent.putExtra("crop", "true")
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            cropIntent.putExtra("outputX", 512)
            cropIntent.putExtra("outputY", 512)
            cropIntent.putExtra("scale", true)
            cropIntent.putExtra("noFaceDetection", true)
            cropIntent.putExtra("return-data", false)

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            val file = File.createTempFile("CROP_${timeStamp}_", ".jpg", storageDir)
            currentPhotoPath = file.absolutePath
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.fitnutrijournal.fileprovider",
                file
            )

            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            // 명시적으로 권한 부여
            val resInfoList = requireContext().packageManager.queryIntentActivities(cropIntent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                requireContext().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            Log.d("cropImage", "Starting crop activity with URI: $photoURI")
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP)
        } catch (e: Exception) {
            Log.e("cropImage", "Error during image cropping: ${e.message}")
            Toast.makeText(requireContext(), "이미지 크롭 작업을 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setPic(imageView: ImageView, photoPath: String) {
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(photoPath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }

        BitmapFactory.decodeFile(photoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun dispatchPickPictureIntent() {
        val pickPictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPictureIntent, REQUEST_IMAGE_PICK)
    }

    private fun setupItemTouchHelper(recyclerView: RecyclerView, adapter: MealWithFoodAdapter) {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                private val background =
                    ColorDrawable(ContextCompat.getColor(requireContext(), R.color.delete_red))
                private val deleteIcon: Drawable? =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
                private val iconMargin = resources.getDimension(R.dimen.icon_margin).toInt()

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val removedItem = adapter.removeItemById(adapter.getItem(position).meal.id)
                    if (removedItem != null) {
                        dietViewModel.deleteMealById(removedItem.meal.id)
                        Toast.makeText(
                            requireContext(),
                            "${removedItem.food.foodName}이 삭제 되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val iconTop =
                        itemView.top + (itemView.height - deleteIcon!!.intrinsicHeight) / 2
                    val iconMargin = iconMargin
                    val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    val iconBottom = iconTop + deleteIcon.intrinsicHeight

                    if (dX < 0) { // Swiping to the left
                        background.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    } else { // view is unswiped
                        background.setBounds(0, 0, 0, 0)
                        deleteIcon.setBounds(0, 0, 0, 0)
                    }

                    background.draw(c)
                    c.save()

                    if (dX < 0) {
                        c.clipRect(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                    } else {
                        c.clipRect(0, 0, 0, 0)
                    }

                    deleteIcon.draw(c)
                    c.restore()
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.refreshFilteredFoods()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + requireContext().packageName)
                startActivity(intent)
            }
        }
        Log.d("camera", "Permissions checked and requested if necessary")
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:" + requireContext().packageName)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(requireContext(), "필요한 권한이 승인되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        Log.d("camera", "Permissions result: $grantResults")
    }


}
