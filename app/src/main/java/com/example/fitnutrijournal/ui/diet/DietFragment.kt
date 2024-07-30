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
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.data.database.FoodDatabase
import com.example.fitnutrijournal.data.repository.FoodRepository
import com.example.fitnutrijournal.data.repository.FoodApiRepository
import com.example.fitnutrijournal.databinding.FragmentDietBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.DietViewModelFactory
import com.example.fitnutrijournal.viewmodel.HomeViewModel
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

        binding.micBtn.setOnClickListener {
            startVoiceInput()
        }

        handleArgs(arguments?.getString("source") ?: "")

        binding.btnAddFood.setOnClickListener {
            val checkedItems = dietViewModel.checkedItems.value ?: emptySet()
            val date = homeViewModel.currentDate.value ?: ""
            val mealType = dietViewModel.mealType.value ?: ""

            if (checkedItems.isEmpty()) {
                Toast.makeText(context, "선택된 아이템이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            homeViewModel.addCheckedItemsToDailyIntakeRecord(checkedItems, date, mealType)

            checkedItems.forEach { food ->
                val quantity = food.servingSize.toFloat()
                Log.d("DietFragment", "Checked items: ${food.foodCd}, Date: $date, Meal type: $mealType, Quantity: $quantity")
            }

            Toast.makeText(context, "음식이 추가되었습니다.", Toast.LENGTH_SHORT).show()

            findNavController().popBackStack()
        }

        binding.btnAddCustomFood.setOnClickListener {
            findNavController().navigate(DietFragmentDirections.actionNavigationDietToCustomAddFragment())
        }


        observeFoodInfo()
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
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

    override fun onResume() {
        super.onResume()
        // Fragment가 다시 생성될 때 체크된 항목 초기화
        dietViewModel.clearCheckedItems()
        dietViewModel.clearSelectedCountFoodItem()
    }

    private fun handleArgs(source: String) {
        when (source) {
            "breakfast", "lunch", "dinner", "snack" -> {
                setUi()
                dietViewModel.setMealType(source)  // mealType 설정
                dietViewModel.setSaveButtonVisibility(true)
                dietViewModel.setUpdateButtonVisibility(false)
                binding.btnAddCustomFood.visibility = View.GONE
            }

            else -> {
                // 네비게이션 바를 통해 접근했을 때 기본 UI
                dietViewModel.setCheckboxVisible(false) // 체크박스 숨김
                dietViewModel.setSaveButtonVisibility(false)
                dietViewModel.setUpdateButtonVisibility(false)
                binding.btnAddFood.visibility = View.GONE
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


        // 체크된 항목 개수 관찰
        dietViewModel.selectedCountFoodItem.observe(viewLifecycleOwner, Observer { count ->
            binding.btnAddFood.text = "${count}개 추가하기"
        })

        dietViewModel.setCheckboxVisible(true) // 체크박스 표시 + 어댑터에서 즐겨찾기 숨김
        dietViewModel.setSaveButtonVisibility(true) // 저장 버튼 표시
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.searchEditText.text.clear()
        _binding = null
    }
}
