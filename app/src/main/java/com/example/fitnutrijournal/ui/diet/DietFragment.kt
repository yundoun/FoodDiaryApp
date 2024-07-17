package com.example.fitnutrijournal.ui.diet

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.data.database.FoodDatabase
import com.example.fitnutrijournal.data.repository.DietRepository
import com.example.fitnutrijournal.data.repository.FoodApiRepository
import com.example.fitnutrijournal.databinding.FragmentDietBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator

class DietFragment : Fragment() {

    private var _binding: FragmentDietBinding? = null
    private val binding get() = _binding!!
    private val dietViewModel: DietViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

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

        val viewPager = binding.viewPager
        val adapter = DietPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        // 실시간 검색을 위한 EditText의 TextWatcher 설정
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                Log.d("DietFragment", "Search query: $query")
                dietViewModel.setSearchQuery(query)
            }
        })

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        handleArgs(arguments?.getString("source") ?: "")

        observeFoodInfo() // 추가된 부분



        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddFood.setOnClickListener {
            val checkedItems = dietViewModel.checkedItems.value ?: emptySet()
            val date = dietViewModel.currentDate.value ?: ""
            val mealType = dietViewModel.mealType.value ?: ""

            checkedItems.forEach { food ->
                val quantity = food.servingSize.toFloat()
                homeViewModel.addCheckedItemsToDailyIntakeRecord(setOf(food), date, mealType)
                Log.d("DietFragment", "Checked items: ${food.foodCd}, Date: $date, Meal type: $mealType, Quantity: $quantity")
            }

            findNavController().popBackStack()
        }



    }

    override fun onResume() {
        super.onResume()
        // Fragment가 다시 생성될 때 체크된 항목 초기화
        dietViewModel.clearCheckedItems()
    }

    private fun handleArgs(source: String) {
        when (source) {
            "breakfast", "lunch", "dinner", "snack" -> {
                setUi()
                dietViewModel.setSaveButtonVisibility(true)

            }

            else -> {
                // 네비게이션 바를 통해 접근했을 때 기본 UI
                dietViewModel.setCheckboxVisible(false) // 체크박스 숨김
                dietViewModel.setSaveButtonVisibility(false)
            }
        }
    }

    private fun observeFoodInfo() {
        val foodDao = FoodDatabase.getDatabase(requireContext()).foodDao()
        val dietRepository = DietRepository(foodDao)
        val foodApiRepository = FoodApiRepository(dietRepository)

        foodApiRepository.fetchFoodInfo().observe(viewLifecycleOwner, Observer { foodResponse ->
            foodResponse?.i2790?.rows?.forEach { item ->
                Log.d(
                    "DietFragment",
                    "식품코드: ${item.foodCd}, 식품군: ${item.groupName}, 식품이름: ${item.foodName}, 총내용량: ${item.servingSize}, 단위: ${item.servingUnit}, 열량: ${item.calories}, 탄수화물: ${item.carbohydrate}, 단백질: ${item.protein}, 지방: ${item.fat}, 당류: ${item.nutrCont5}, 나트륨: ${item.nutrCont6}, 콜레스테롤: ${item.nutrCont7}, 포화지방산: ${item.nutrCont8}, 트랜스지방: ${item.nutrCont9}"
                )
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setUi() {
        // 음식 추가 관련 UI 조정
        (activity as MainActivity).showBottomNavigation(false)

        val layoutParams = binding.btnAddFood.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = 50
        binding.btnAddFood.layoutParams = layoutParams

        // 체크된 항목 개수 관찰
        dietViewModel.selectedCountFoodItem.observe(viewLifecycleOwner, Observer { count ->
            binding.btnAddFood.text = "${count}개 추가하기"
        })

        dietViewModel.setCheckboxVisible(true) // 체크박스 표시 + 어댑터에서 즐겨찾기 숨김
        dietViewModel.setSaveButtonVisibility(true) // 저장 버튼 표시
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
