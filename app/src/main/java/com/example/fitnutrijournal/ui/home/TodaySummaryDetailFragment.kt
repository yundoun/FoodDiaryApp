package com.example.fitnutrijournal.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.PorterDuff
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.DialogCalorieInputBinding
import com.example.fitnutrijournal.databinding.FragmentTodaySummaryDetailBinding
import com.example.fitnutrijournal.ui.home.Tab.ViewPagerAdapter
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("SetTextI18n")
@RequiresApi(Build.VERSION_CODES.O)
class TodaySummaryDetailFragment : Fragment() {

    private var _binding: FragmentTodaySummaryDetailBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodaySummaryDetailBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).showBottomNavigation(false)
        binding.viewPager.adapter = ViewPagerAdapter(this)


        setProgressbarColor()

        setClickListener()
        setUiObserve()
        setTabLayoutMediator()
    }

    private fun setProgressbarColor() {

        setupCombinedIntakeObserver(
            homeViewModel.currentTotalCalories,
            homeViewModel.targetCalories,
            binding.calorieProgressBar
        )

        setupCombinedIntakeObserver(
            homeViewModel.currentCarbIntake,
            homeViewModel.targetCarbIntake,
            binding.carbProgressBar
        )

        setupCombinedIntakeObserver(
            homeViewModel.currentProteinIntake,
            homeViewModel.targetProteinIntake,
            binding.proteinProgressBar
        )

        setupCombinedIntakeObserver(
            homeViewModel.currentFatIntake,
            homeViewModel.targetFatIntake,
            binding.fatProgressBar
        )
    }

    private fun setupCombinedIntakeObserver(
        currentIntakeLiveData: LiveData<Int>,
        targetIntakeLiveData: LiveData<Int>,
        progressBar: ProgressBar
    ) {
        val combinedIntake = MediatorLiveData<Pair<Int, Int>>().apply {
            var currentIntakeValue = currentIntakeLiveData.value ?: 0
            var targetIntakeValue = targetIntakeLiveData.value ?: 0

            addSource(currentIntakeLiveData) { currentIntake ->
                currentIntakeValue = currentIntake
                value = currentIntakeValue to targetIntakeValue
            }

            addSource(targetIntakeLiveData) { targetIntake ->
                targetIntakeValue = targetIntake
                value = currentIntakeValue to targetIntakeValue
            }
        }

        combinedIntake.observe(viewLifecycleOwner, Observer { (currentIntake, targetIntake) ->
            Log.d("progressbar", "Intake: $currentIntake Target: $targetIntake")
            updateProgressBarColor(progressBar, currentIntake, targetIntake)
        })
    }

    private fun updateProgressBarColor(progressBar: ProgressBar, currentIntake: Int, targetIntake: Int) {
        val progressDrawable = progressBar.progressDrawable.mutate() as LayerDrawable
        val progressLayer = progressDrawable.findDrawableByLayerId(android.R.id.progress) as ClipDrawable
        val colorResId = if (currentIntake > targetIntake) {
            R.color.progressbar_red
        } else {
            R.color.progressbar_green
        }
        progressLayer.setColorFilter(ContextCompat.getColor(requireContext(), colorResId), PorterDuff.Mode.SRC_IN)
        progressBar.progressDrawable = progressDrawable
    }

    private fun setClickListener(){
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.targetEdit.setOnClickListener {
            showCalorieInputDialog()
        }
    }

    private fun setUiObserve() {
        homeViewModel.todayGoal.observe(viewLifecycleOwner) { goal ->
            goal?.let {
                binding.tvTargetCalories.text = "${it.targetCalories} kcal"
                binding.breakfast.text = "${it.targetBreakfast} kcal"
                binding.lunch.text = "${it.targetLunch} kcal"
                binding.dinner.text = "${it.targetDinner} kcal"
                binding.snack.text = "${it.targetSnack} kcal"
            }
        }
    }

    private fun setTabLayoutMediator() {
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
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}