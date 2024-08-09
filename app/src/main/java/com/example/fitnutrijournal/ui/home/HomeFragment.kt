package com.example.fitnutrijournal.ui.home

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RotateDrawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.FragmentHomeBinding
import com.example.fitnutrijournal.ui.Activity.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.DietViewModelFactory
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.example.fitnutrijournal.viewmodel.MemoViewModel
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val memoViewModel: MemoViewModel by activityViewModels()
    private val dietViewModel: DietViewModel by viewModels {
        DietViewModelFactory(requireActivity().application, homeViewModel)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        (activity as MainActivity).showBottomNavigation(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setProgressbarColor()
        setupNavigation()
        setupCircularProgressBarColorObserver()
    }

    private fun setupObservers() {
        homeViewModel.remainingCalories.observe(viewLifecycleOwner) {
            setRemainingCaloriesText(binding.remainingCalories, it)
            setCalorieIntakeText(
                binding.tvCalorieIntake,
                homeViewModel.dailyIntakeRecord.value?.currentCalories ?: 0
            )
            setTargetCaloriesText(
                binding.tvTargetCalories,
                homeViewModel.todayGoal.value?.targetCalories ?: 0
            )
        }

        homeViewModel.currentDate.observe(viewLifecycleOwner) { date ->
            memoViewModel.loadMemoByDate(date)
        }


        memoViewModel.clickedDateMemo.observe(viewLifecycleOwner) { memo ->
            binding.edtMemo.text = memo?.content?.ifEmpty {
                "작성된 메모가 없습니다"
            } ?: ""
        }
    }


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    @BindingAdapter("calorieIntakeText")
    fun setCalorieIntakeText(textView: TextView, currentCalories: Int) {
        val context = textView.context
        val intakeText = "섭취량"
        val displayText = "$currentCalories\n$intakeText"
        val spannable = SpannableString(displayText)

        // "섭취량" 텍스트의 색상 변경
        val grayColor = ContextCompat.getColor(context, R.color.text_gray)
        spannable.setSpan(
            ForegroundColorSpan(grayColor),
            displayText.indexOf(intakeText),
            displayText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // "섭취량" 텍스트의 크기 변경
        val scale = context.resources.displayMetrics.density
        val pixelSize = (12 * scale + 0.5f).toInt()
        spannable.setSpan(
            AbsoluteSizeSpan(pixelSize),
            displayText.indexOf(intakeText),
            displayText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannable
    }


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    @BindingAdapter("targetCaloriesText")
    fun setTargetCaloriesText(textView: TextView, targetCalories: Int) {
        val context = textView.context
        val targetText = "목표"
        val displayText = "$targetCalories\n$targetText"
        val spannable = SpannableString(displayText)

        // "목표" 텍스트의 색상 변경
        val grayColor = ContextCompat.getColor(context, R.color.text_gray)
        spannable.setSpan(
            ForegroundColorSpan(grayColor),
            displayText.indexOf(targetText),
            displayText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // "목표" 텍스트의 크기 변경
        val scale = context.resources.displayMetrics.density
        val pixelSize = (12 * scale + 0.5f).toInt()
        spannable.setSpan(
            AbsoluteSizeSpan(pixelSize),
            displayText.indexOf(targetText),
            displayText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannable
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    @BindingAdapter("remainingCaloriesText")
    fun setRemainingCaloriesText(textView: TextView, remainingCalories: Int) {
        Log.d("HomeFragment", "Remaining Calories: $remainingCalories")
        val context = textView.context
        val unitText: String
        val displayText: String

        if (remainingCalories < 0) {
            unitText = "초과량"
            displayText = "${abs(remainingCalories)}\n$unitText"
            textView.setTextColor(ContextCompat.getColor(context, R.color.progressbar_red))
        } else {
            unitText = "잔여량"
            displayText = "$remainingCalories\n$unitText"
            textView.setTextColor(ContextCompat.getColor(context, R.color.progressbar_blue))
        }

        val spannable = SpannableString(displayText)

        // "초과량" 또는 "잔여량" 텍스트의 색상 변경
        val grayColor = ContextCompat.getColor(context, R.color.text_gray)
        spannable.setSpan(
            ForegroundColorSpan(grayColor),
            displayText.indexOf(unitText),
            displayText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // "초과량" 또는 "잔여량" 텍스트의 크기 변경
        val scale = context.resources.displayMetrics.density
        val pixelSize = (12 * scale + 0.5f).toInt()
        spannable.setSpan(
            AbsoluteSizeSpan(pixelSize),
            displayText.indexOf(unitText),
            displayText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannable
    }


    private fun setupNavigation() {
        binding.apply {
            memoCardView.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_diaryFragment)
            }
            calendarLayout.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_calendarFragment)
            }
            btnTodaySummaryDetail.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_todaySummaryDetailFragment)
            }
            todaySummarySection.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_todaySummaryDetailFragment)
            }
            breakfastLayout.setOnClickListener {
                navigateToMealDetail("breakfast")
            }
            lunchLayout.setOnClickListener {
                navigateToMealDetail("lunch")
            }
            dinnerLayout.setOnClickListener {
                navigateToMealDetail("dinner")
            }
            snackLayout.setOnClickListener {
                navigateToMealDetail("snack")
            }
            addBreakfast.setOnClickListener {
                navigateToDiet("breakfast")
            }
            addLunch.setOnClickListener {
                navigateToDiet("lunch")
            }
            addDinner.setOnClickListener {
                navigateToDiet("dinner")
            }
            addSnack.setOnClickListener {
                navigateToDiet("snack")
            }
        }
    }

    private fun setProgressbarColor() {
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

    private fun updateProgressBarColor(
        progressBar: ProgressBar,
        currentIntake: Int,
        targetIntake: Int
    ) {
        val progressDrawable = progressBar.progressDrawable.mutate() as LayerDrawable
        val progressLayer =
            progressDrawable.findDrawableByLayerId(android.R.id.progress) as ClipDrawable
        val colorResId = when {
            currentIntake > targetIntake -> R.color.progressbar_red
            currentIntake > targetIntake * 0.8 -> R.color.progressbar_orange
            else -> R.color.progressbar_green
        }
        progressLayer.setColorFilter(
            ContextCompat.getColor(requireContext(), colorResId),
            PorterDuff.Mode.SRC_IN
        )
        progressBar.progressDrawable = progressDrawable
    }

    private fun setupCircularProgressBarColorObserver() {
        val combinedIntake = MediatorLiveData<Pair<Int, Int>>().apply {
            var currentTotalCalories = homeViewModel.currentTotalCalories.value ?: 0
            var targetCalories = homeViewModel.todayGoal.value?.targetCalories ?: 0

            addSource(homeViewModel.currentTotalCalories) { currentCalories ->
                currentTotalCalories = currentCalories
                value = currentTotalCalories to targetCalories
            }

            addSource(homeViewModel.todayGoal) { todayGoal ->
                targetCalories = todayGoal?.targetCalories ?: 0
                value = currentTotalCalories to targetCalories
            }
        }

        combinedIntake.observe(
            viewLifecycleOwner,
            Observer { (currentTotalCalories, targetCalories) ->
                Log.d(
                    "circularProgressBar",
                    "Current Total Calories: $currentTotalCalories, Target Calories: $targetCalories"
                )
                updateCircularProgressBarColor(
                    binding.circularProgressBar,
                    currentTotalCalories,
                    targetCalories
                )
            })
    }

    private fun updateCircularProgressBarColor(
        progressBar: ProgressBar,
        currentTotalCalories: Int,
        targetCalories: Int
    ) {
        val progressDrawable = progressBar.progressDrawable.mutate()

        if (progressDrawable is LayerDrawable) {
            val progressLayer = progressDrawable.findDrawableByLayerId(android.R.id.progress)

            if (progressLayer is RotateDrawable) {
                val drawable = progressLayer.drawable
                val colorResId = when {
                    currentTotalCalories > targetCalories -> R.color.progressbar_red
                    currentTotalCalories > targetCalories * 0.8 -> R.color.progressbar_orange
                    else -> R.color.progressbar_blue
                }
                drawable?.setColorFilter(
                    ContextCompat.getColor(requireContext(), colorResId),
                    PorterDuff.Mode.SRC_IN
                )
                progressBar.progressDrawable = progressDrawable
            } else {
                Log.e("HomeFragment", "Progress layer is not a RotateDrawable")
            }
        } else {
            Log.e("HomeFragment", "Progress drawable is not a LayerDrawable")
        }
    }


    private fun navigateToMealDetail(mealType: String) {
        homeViewModel.setMealType(mealType)
        val action = HomeFragmentDirections.actionNavigationHomeToMealDetailFragment()
        findNavController().navigate(action)
    }

    private fun navigateToDiet(mealType: String) {
        dietViewModel.setMealType(mealType)
        findNavController().navigate(
            HomeFragmentDirections.actionNavigationHomeToNavigationDiet(mealType)
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

