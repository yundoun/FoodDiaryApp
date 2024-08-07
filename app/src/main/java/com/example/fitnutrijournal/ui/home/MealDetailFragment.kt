package com.example.fitnutrijournal.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.model.MealWithFood
import com.example.fitnutrijournal.databinding.FragmentMealDetailBinding
import com.example.fitnutrijournal.data.adapter.MealWithFoodAdapter
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.utils.CameraHelper
import com.example.fitnutrijournal.utils.PhotoViewActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.DietViewModelFactory
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.example.fitnutrijournal.viewmodel.PhotoViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MealDetailFragment : Fragment() {

    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!
    private val photoViewModel: PhotoViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val dietViewModel: DietViewModel by activityViewModels {
        DietViewModelFactory(requireActivity().application, homeViewModel)
    }


    private val REQUEST_PERMISSIONS = 1001
    private lateinit var cameraHelper: CameraHelper
    private var photoUri: String? = null
    private var photoId: Long? = null

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

        val currentDate = homeViewModel.currentDate.value ?: LocalDate.now().format(homeViewModel.dateFormatter)
        val mealType = homeViewModel.mealType.value ?: "breakfast"
        cameraHelper = CameraHelper(this, binding.imageSample,binding.imageSampleLayout , photoViewModel, currentDate, mealType)

        dietViewModel.setAddFromLibraryButtonVisibility(false)

        clearCheckedItems()

        // 날짜와 식사 타입에 따른 사진 바인딩
        bindPhoto()

        setClickListeners()

        dietViewModel.setCheckboxVisible(false)

        setupRecyclerView()

        observeViewModels()
    }

    private fun clearCheckedItems() {
        dietViewModel.clearCheckedItems()
        dietViewModel.clearSelectedCountFoodItem()
    }

    private fun setClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddFood.setOnClickListener {
            val source = homeViewModel.mealType.value ?: "breakfast"
            val action =
                MealDetailFragmentDirections.actionMealDetailFragmentToNavigationDiet(source)
            findNavController().navigate(action)
        }

        binding.cameraBtn.setOnClickListener {
            showImageSourceDialog()
        }

        binding.imageSample.setOnClickListener {
            Log.d("PhotoViewActivity", "Photo URI: $photoUri")
            photoUri?.let {
                val intent = Intent(requireContext(), PhotoViewActivity::class.java)
                intent.putExtra(PhotoViewActivity.EXTRA_PHOTO_URI, it)
                Log.d("PhotoViewActivity", "Photo URI: $it")
                startActivity(intent)
            }
        }

        binding.menuBtn.setOnClickListener {
            showOptionsMenu()
        }
    }

    private fun showOptionsMenu() {
        val options = arrayOf("사진 삭제")
        AlertDialog.Builder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showDeletePhotoDialog() // 사진 삭제
                }
            }
            .show()
    }

    private fun showDeletePhotoDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("사진 삭제")
            .setMessage("이 사진을 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                Log.d("PhotoViewActivity", "Photo ID: $photoId")
                photoId?.let {
                    photoViewModel.deletePhotoById(it)
                    binding.imageSample.setImageResource(R.drawable.ic_camera_background)
                    binding.imageSampleLayout.visibility = View.VISIBLE
                    binding.imageSample.visibility = View.GONE
                    photoUri = null
                    Toast.makeText(requireContext(), "사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("아니오", null)
            .show()
    }

    private fun observeViewModels() {
        homeViewModel.mealType.observe(viewLifecycleOwner) { mealType ->
            dietViewModel.setMealType(mealType)
            homeViewModel.filterFoodsByMealType(mealType)
            updateMealText(mealType)
        }

        homeViewModel.filteredFoods.observe(viewLifecycleOwner) { foods ->
            viewLifecycleOwner.lifecycleScope.launch {
                val uniqueMeals = mutableListOf<MealWithFood>()
                val date = homeViewModel.currentDate.value ?: LocalDate.now().format(homeViewModel.dateFormatter)
                val mealType = homeViewModel.mealType.value ?: "breakfast"
                val meals = homeViewModel.mealRepository.getMealsByDateAndTypeSync(date, mealType)
                meals.forEach { meal ->
                    val food = foods.find { it.foodCd == meal.dietFoodCode }
                    if (food != null) {
                        uniqueMeals.add(MealWithFood(meal = meal, food = food))
                    }
                }
                Log.d("MealDetailFragment", "Updating Adapter with Meals: $uniqueMeals")
                (binding.foodList.adapter as MealWithFoodAdapter).updateMealsWithFood(uniqueMeals)
            }
        }

        dietViewModel.loadMealsWithFood()
    }

    @SuppressLint("SetTextI18n")
    private fun updateMealText(mealType: String) {
        val mealText = when (mealType) {
            "breakfast" -> {
                observeMealNutrition(homeViewModel.currentCaloriesBreakfast, homeViewModel.currentCarbIntakeBreakfast, homeViewModel.currentProteinIntakeBreakfast, homeViewModel.currentFatIntakeBreakfast)
                "아침"
            }
            "lunch" -> {
                observeMealNutrition(homeViewModel.currentCaloriesLunch, homeViewModel.currentCarbIntakeLunch, homeViewModel.currentProteinIntakeLunch, homeViewModel.currentFatIntakeLunch)
                "점심"
            }
            "dinner" -> {
                observeMealNutrition(homeViewModel.currentCaloriesDinner, homeViewModel.currentCarbIntakeDinner, homeViewModel.currentProteinIntakeDinner, homeViewModel.currentFatIntakeDinner)
                "저녁"
            }
            "snack" -> {
                observeMealNutrition(homeViewModel.currentCaloriesSnack, homeViewModel.currentCarbIntakeSnack, homeViewModel.currentProteinIntakeSnack, homeViewModel.currentFatIntakeSnack)
                "간식"
            }
            else -> "식사"
        }
        binding.mealType.text = mealText
    }

    @SuppressLint("SetTextI18n")
    private fun observeMealNutrition(caloriesLiveData: LiveData<Int>, carbLiveData: LiveData<Int>, proteinLiveData: LiveData<Int>, fatLiveData: LiveData<Int>) {
        caloriesLiveData.observe(viewLifecycleOwner) { calories ->
            binding.calories.text = "$calories kcal\n총 섭취량"
        }
        carbLiveData.observe(viewLifecycleOwner) { carb ->
            binding.carb.text = "$carb g\n탄수화물"
        }
        proteinLiveData.observe(viewLifecycleOwner) { protein ->
            binding.protein.text = "$protein g\n단백질"
        }
        fatLiveData.observe(viewLifecycleOwner) { fat ->
            binding.fat.text = "$fat g\n지방"
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.foodList
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        // DividerItemDecoration 추가
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        val adapter = MealWithFoodAdapter(
            emptyList(),
            { mealWithFood ->
                val mealId = mealWithFood.meal.id
                dietViewModel.selectFood(mealWithFood.food.foodCd)
                dietViewModel.setSaveButtonVisibility(false)
                dietViewModel.setUpdateButtonVisibility(true)

                val action = MealDetailFragmentDirections
                    .actionMealDetailFragmentToFoodDetailFragment(mealId)
                findNavController().navigate(action)
            },
            dietViewModel
        )
        recyclerView.adapter = adapter
        setupItemTouchHelper(recyclerView, adapter)
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("카메라로 촬영")
        val builder = AlertDialog.Builder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        cameraHelper.dispatchTakePictureIntent()
                    }
                }
            }
        builder.create().show()
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        cameraHelper.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupItemTouchHelper(recyclerView: RecyclerView, adapter: MealWithFoodAdapter) {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

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
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    adapter.moveItem(fromPosition, toPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val removedItem = adapter.removeItemById(adapter.getItem(position).meal.id)
                    if (removedItem != null) {
                        dietViewModel.deleteMealById(removedItem.meal.id)
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



    private fun bindPhoto() {
        val date = homeViewModel.currentDate.value ?: LocalDate.now().format(homeViewModel.dateFormatter)
        val mealType = homeViewModel.mealType.value ?: "breakfast"
        photoViewModel.getPhotoByDateAndMealType(date, mealType).observe(viewLifecycleOwner) { photo ->
            if (photo?.photoUri != null) {
                photoUri = photo.photoUri
                photoId = photo.id
                Glide.with(this)
                    .load(photo.photoUri)
                    .into(binding.imageSample)
                // 사진이 존재할 경우
                binding.imageSampleLayout.visibility = View.GONE
                binding.imageSample.visibility = View.VISIBLE
                binding.cameraBtn.visibility = View.GONE
                binding.menuBtn.visibility = View.VISIBLE
            } else {
                photoUri = null
                photoId = null
                binding.imageSample.setImageResource(R.drawable.ic_camera_background)
                // 사진이 존재하지 않을 경우
                binding.imageSampleLayout.visibility = View.VISIBLE
                binding.imageSample.visibility = View.GONE
                binding.cameraBtn.visibility = View.VISIBLE
                binding.menuBtn.visibility = View.GONE
            }
        }
    }
}
