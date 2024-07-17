package com.example.fitnutrijournal.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.databinding.DialogCalorieInputBinding
import com.example.fitnutrijournal.databinding.FragmentTodaySummaryDetailBinding
import com.example.fitnutrijournal.ui.home.Tab.ViewPagerAdapter
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class TodaySummaryDetailFragment : Fragment() {

    private var _binding: FragmentTodaySummaryDetailBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodaySummaryDetailBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        (activity as MainActivity).showBottomNavigation(false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.targetEdit.setOnClickListener {
            showCalorieInputDialog()
        }

        // 목표 데이터를 관찰하여 UI 업데이트
        homeViewModel.todayGoal.observe(viewLifecycleOwner) { goal ->
            goal?.let {
                // 필요한 경우 UI 업데이트 코드 작성
                binding.tvTargetCalories.text = "목표 칼로리\n${it.targetCalories} kcal"
                binding.breakfast.text = "${it.targetBreakfast} kcal"
                binding.lunch.text = "${it.targetLunch} kcal"
                binding.dinner.text = "${it.targetDinner} kcal"
                binding.snack.text = "${it.targetSnack} kcal"
            }
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun showCalorieInputDialog() {
        val dialogView = DialogCalorieInputBinding.inflate(LayoutInflater.from(context))

        val morningCaloriesInput = dialogView.morningCalories
        val lunchCaloriesInput = dialogView.lunchCalories
        val dinnerCaloriesInput = dialogView.dinnerCalories
        val snackCaloriesInput = dialogView.snackCalories
        val totalCaloriesText = dialogView.totalCalories

        //  EditText에 TextWatcher를 추가하여 사용자가 값을 입력할 때마다 updateTotalCalories 함수를 호출
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateTotalCalories(
                    morningCaloriesInput,
                    lunchCaloriesInput,
                    dinnerCaloriesInput,
                    snackCaloriesInput,
                    totalCaloriesText
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        morningCaloriesInput.addTextChangedListener(textWatcher)
        lunchCaloriesInput.addTextChangedListener(textWatcher)
        dinnerCaloriesInput.addTextChangedListener(textWatcher)
        snackCaloriesInput.addTextChangedListener(textWatcher)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView.root)
            .setTitle("목표를 설정하세요")
            .setPositiveButton("저장") { _, _ ->
                val morningCalories = morningCaloriesInput.text.toString().toIntOrNull() ?: 0
                val lunchCalories = lunchCaloriesInput.text.toString().toIntOrNull() ?: 0
                val dinnerCalories = dinnerCaloriesInput.text.toString().toIntOrNull() ?: 0
                val snackCalories = snackCaloriesInput.text.toString().toIntOrNull() ?: 0

                val totalCalories = morningCalories + lunchCalories + dinnerCalories + snackCalories

                val date = homeViewModel.selectedDate.value ?: LocalDate.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                homeViewModel.saveDailyIntakeGoal(
                    date,
                    totalCalories,
                    morningCalories,
                    lunchCalories,
                    dinnerCalories,
                    snackCalories
                )
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun updateTotalCalories(
        morningCaloriesInput: EditText,
        lunchCaloriesInput: EditText,
        dinnerCaloriesInput: EditText,
        snackCaloriesInput: EditText,
        totalCaloriesText: TextView
    ) {
        val morningCalories = morningCaloriesInput.text.toString().toIntOrNull() ?: 0
        val lunchCalories = lunchCaloriesInput.text.toString().toIntOrNull() ?: 0
        val dinnerCalories = dinnerCaloriesInput.text.toString().toIntOrNull() ?: 0
        val snackCalories = snackCaloriesInput.text.toString().toIntOrNull() ?: 0

        val totalCalories = morningCalories + lunchCalories + dinnerCalories + snackCalories
        totalCaloriesText.text = "목표 칼로리: $totalCalories"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "아침"
                1 -> "점심"
                2 -> "저녁"
                3 -> "간식"
                else -> "Breakfast"
            }
        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}