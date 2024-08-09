package com.example.fitnutrijournal.ui.diet

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.adapter.DietPagerAdapter
import com.example.fitnutrijournal.data.database.FoodDatabase
import com.example.fitnutrijournal.data.repository.FoodApiRepository
import com.example.fitnutrijournal.data.repository.FoodRepository
import com.example.fitnutrijournal.databinding.FragmentDietBinding
import com.example.fitnutrijournal.ui.Activity.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.DietViewModelFactory
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale

class DietFragment : Fragment() {

    private var _binding: FragmentDietBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val dietViewModel: DietViewModel by activityViewModels {
        DietViewModelFactory(requireActivity().application, homeViewModel)
    }

    private val REQUEST_CODE_SPEECH_INPUT = 100
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var shouldClearCheckedItems = true
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietBinding.inflate(inflater, container, false).apply {
            viewModel = dietViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        (activity as MainActivity).showBottomNavigation(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val adapter = DietPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()



        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            shouldClearCheckedItems = when (destination.id) {
                R.id.foodDetailFragment -> false
                else -> true
            }
        }

        // 정렬 버튼 클릭 시 팝업 메뉴 표시
        binding.btnSort.setOnClickListener { showSortMenu(it) }
        binding.btnMenu.setOnClickListener { showMenu(it) }

        // 실시간 검색을 위한 EditText의 TextWatcher 설정
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                dietViewModel.setSearchQuery(query)
            }
        })

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.micBtn.setOnClickListener {
            startVoiceInput()
        }

        handleArgs(arguments?.getString("source") ?: "")

        binding.btnAddFood.setOnClickListener {
            val checkedItems = dietViewModel.checkedItems.value ?: emptySet()
            val date = homeViewModel.currentDate.value ?: ""
            val mealType = dietViewModel.mealType.value ?: ""

            if (checkedItems.isEmpty()) {
                Snackbar.make(view, "선택된 아이템이 없습니다.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            homeViewModel.addCheckedItemsToDailyIntakeRecord(checkedItems, date, mealType)

            checkedItems.forEach { food ->
                val quantity = food.servingSize.toFloat()
                Log.d(
                    "DietFragment",
                    "Checked items: ${food.foodCd}, Date: $date, Meal type: $mealType, Quantity: $quantity"
                )
            }

            dietViewModel.clearCheckedItems()
            dietViewModel.clearSelectedCountFoodItem()
            Snackbar.make(view, "음식이 추가되었습니다.", Snackbar.LENGTH_SHORT).show()
        }

        //observeFoodInfo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        if (this::viewPager.isInitialized && shouldClearCheckedItems) {
            clearCheckedItemsInAllTabs()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clearCheckedItemsInAllTabs() {
        val adapter = viewPager.adapter as DietPagerAdapter
        for (i in 0 until adapter.itemCount) {
            val fragment = childFragmentManager.findFragmentByTag("f$i") as? DietTabFragment
            fragment?.clearCheckedItems()
            fragment?.clearSelectedCountFoodItem()
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "음성을 입력하세요")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "음성 인식을 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                val recognizedText = result[0]
                binding.searchEditText.setText(recognizedText)
                dietViewModel.setSearchQuery(recognizedText)
            }
        }
    }

    private fun handleArgs(source: String) {
        when (source) {
            "breakfast", "lunch", "dinner", "snack" -> {
                setUi()
                dietViewModel.setMealType(source)  // mealType 설정
                dietViewModel.setCheckboxVisible(true)
                dietViewModel.setFavoriteButtonVisibility(false)

                dietViewModel.setSaveButtonVisibility(true)
                dietViewModel.setUpdateButtonVisibility(false)
                dietViewModel.setAddFromLibraryButtonVisibility(false)
                dietViewModel.setLongClickEnabled(false)
            }

            else -> {
                // 네비게이션 바를 통해 접근했을 때 기본 UI
                dietViewModel.setCheckboxVisible(false) // 체크박스 숨김
                dietViewModel.setFavoriteButtonVisibility(true)

                dietViewModel.setSaveButtonVisibility(false)
                dietViewModel.setUpdateButtonVisibility(false)
                dietViewModel.setAddFromLibraryButtonVisibility(true)
                binding.btnAddFood.visibility = View.GONE

                dietViewModel.setLongClickEnabled(true)
            }
        }
    }

    private fun observeFoodInfo() {
        Log.d("DietFragment", "observeFoodInfo() 메소드 호출됨")
        val foodDao = FoodDatabase.getDatabase(requireContext()).foodDao()
        val foodRepository = FoodRepository(foodDao)
        val foodApiRepository = FoodApiRepository(foodRepository)

        foodApiRepository.fetchFoodInfo().observe(viewLifecycleOwner, Observer { foodResponse ->
            foodResponse?.i2790?.rows?.forEach { item ->
                //Log.d("DietFragment", "Api 호출 관찰됨 ")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setUi() {
        // 음식 추가 관련 UI 조정
        (activity as MainActivity).showBottomNavigation(false)


        // 체크된 항목 개수 관찰
        dietViewModel.selectedCountFoodItem.observe(viewLifecycleOwner, Observer { count ->
            binding.btnAddFood.text = "${count}개 추가하기"
        })

        dietViewModel.setCheckboxVisible(true) // 체크박스 표시 + 어댑터에서 즐겨찾기 숨김
        dietViewModel.setSaveButtonVisibility(true) // 저장 버튼 표시
    }


    private fun showSortMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.sort_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.sort_ascending -> {
                    dietViewModel.setSortOrder(DietViewModel.SortOrder.ASCENDING)
                    true
                }
                R.id.sort_descending -> {
                    dietViewModel.setSortOrder(DietViewModel.SortOrder.DESCENDING)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showMenu(view: View){
        val popup = PopupMenu(requireContext(),view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.diet_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.add_food -> {
                    findNavController().navigate(DietFragmentDirections.actionNavigationDietToCustomAddFragment())
                    true
                }
                else -> false
            }
        }
        popup.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.searchEditText.text.clear()
        _binding = null
    }
}
