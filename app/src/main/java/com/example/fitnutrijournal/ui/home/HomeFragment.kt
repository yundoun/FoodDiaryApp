package com.example.fitnutrijournal.ui.home

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.FragmentHomeBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.DietViewModelFactory
import com.example.fitnutrijournal.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
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

        setProgressbarColor()
        setupNavigation()
    }

    private fun setupNavigation() {
        binding.apply {
            btnCalendar.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_calendarFragment)
            }
            btnTodaySummaryDetail.setOnClickListener {
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
