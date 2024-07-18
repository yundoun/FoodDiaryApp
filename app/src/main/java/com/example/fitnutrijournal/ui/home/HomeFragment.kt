package com.example.fitnutrijournal.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.FragmentHomeBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val dietViewModel: DietViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        ( activity as MainActivity).showBottomNavigation(true)
        // 로그 추가
        homeViewModel.targetCalories.observe(viewLifecycleOwner) { value ->
            Log.d("HomeFragment", "Observed targetCalories: $value")
        }
        homeViewModel.targetCarbIntake.observe(viewLifecycleOwner) { value ->
            Log.d("HomeFragment", "Observed targetCarbIntake: $value")
        }
        homeViewModel.targetProteinIntake.observe(viewLifecycleOwner) { value ->
            Log.d("HomeFragment", "Observed targetProteinIntake: $value")
        }
        homeViewModel.targetFatIntake.observe(viewLifecycleOwner) { value ->
            Log.d("HomeFragment", "Observed targetFatIntake: $value")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        homeViewModel.todayGoal.observe(viewLifecycleOwner) { goal ->
            goal?.let {
                binding.tvTargetCalories.text = "목표 칼로리\n${it.targetCalories} kcal"
            }
        }


        homeViewModel.breakfastNutrients.observe(viewLifecycleOwner) { nutrients ->
            // update UI with breakfast nutrients
//            binding.tvBreakfastCalories.text = "아침 칼로리: ${nutrients.calories}"
//            binding.tvBreakfastCarbs.text = "아침 탄수화물: ${nutrients.carbs}"
//            binding.tvBreakfastProtein.text = "아침 단백질: ${nutrients.protein}"
//            binding.tvBreakfastFat.text = "아침 지방: ${nutrients.fat}"
            binding.tvBreakfastCalories.text = "아침 칼로리: ${nutrients.calories} kcal"
        }


        homeViewModel.lunchNutrients.observe(viewLifecycleOwner) { nutrients ->
            binding.tvLunchCalories.text = "점심 칼로리: ${nutrients.calories} kcal"
        }

        homeViewModel.dinnerNutrients.observe(viewLifecycleOwner) { nutrients ->
            binding.tvDinnerCalories.text = "저녁 칼로리: ${nutrients.calories} kcal"
        }

        homeViewModel.snackNutrients.observe(viewLifecycleOwner) { nutrients ->
            binding.tvSnackCalories.text = "간식 칼로리: ${nutrients.calories} kcal"
        }

        binding.btnCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_calendarFragment)
        }

        binding.btnTodaySummaryDetail.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_todaySummaryDetailFragment)
        }

        binding.btnCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_calendarFragment)
        }

        binding.btnTodaySummaryDetail.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_todaySummaryDetailFragment)
        }

        binding.addBreakfast.setOnClickListener {
            dietViewModel.setMealType("breakfast")
            dietViewModel.setCurrentDate(homeViewModel.currentDate.value ?: "")
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationDiet("breakfast"))
        }

        binding.addLunch.setOnClickListener {
            dietViewModel.setMealType("lunch")
            dietViewModel.setCurrentDate(homeViewModel.currentDate.value ?: "")
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationDiet("lunch"))
        }

        binding.addDinner.setOnClickListener {
            dietViewModel.setMealType("dinner")
            dietViewModel.setCurrentDate(homeViewModel.currentDate.value ?: "")
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationDiet("dinner"))
        }

        binding.addSnack.setOnClickListener {
            dietViewModel.setMealType("snack")
            dietViewModel.setCurrentDate(homeViewModel.currentDate.value ?: "")
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationDiet("snack"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
