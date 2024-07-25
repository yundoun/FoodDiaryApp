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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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

        // 아침, 점심, 저녁, 간식 레이아웃 클릭 이벤트 설정
        binding.breakfastLayout.setOnClickListener {
            navigateToMealDetail("breakfast")
        }

        binding.lunchLayout.setOnClickListener {
            navigateToMealDetail("lunch")
        }

        binding.dinnerLayout.setOnClickListener {
            navigateToMealDetail("dinner")
        }

        binding.snackLayout.setOnClickListener {
            navigateToMealDetail("snack")
        }

        binding.addBreakfast.setOnClickListener {
            dietViewModel.setMealType("breakfast")
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToNavigationDiet(
                    "breakfast"
                )
            )
        }

        binding.addLunch.setOnClickListener {
            dietViewModel.setMealType("lunch")
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToNavigationDiet(
                    "lunch"
                )
            )
        }

        binding.addDinner.setOnClickListener {
            dietViewModel.setMealType("dinner")
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToNavigationDiet(
                    "dinner"
                )
            )
        }

        binding.addSnack.setOnClickListener {
            dietViewModel.setMealType("snack")
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToNavigationDiet(
                    "snack"
                )
            )
        }


        setProgressbarColor()


    }

    private fun setProgressbarColor(){

        homeViewModel.currentCarbIntake.observe(viewLifecycleOwner, Observer { currentIntake ->
            val targetIntake = homeViewModel.targetCarbIntake.value ?: 0 // 기본값을 0으로 설정

            if (currentIntake > targetIntake) {
                val progressDrawable = binding.carbProgressBar.progressDrawable.mutate() as LayerDrawable
                val progressLayer = progressDrawable.findDrawableByLayerId(android.R.id.progress) as ClipDrawable
                progressLayer.setColorFilter(ContextCompat.getColor(requireContext(), R.color.progressbar_red), PorterDuff.Mode.SRC_IN)
                binding.carbProgressBar.progressDrawable = progressDrawable
            } else {
                val progressDrawable = binding.carbProgressBar.progressDrawable.mutate() as LayerDrawable
                val progressLayer = progressDrawable.findDrawableByLayerId(android.R.id.progress) as ClipDrawable
                progressLayer.setColorFilter(ContextCompat.getColor(requireContext(), R.color.progressbar_green), PorterDuff.Mode.SRC_IN)
                binding.carbProgressBar.progressDrawable = progressDrawable
            }
        })

        homeViewModel.currentProteinIntake.observe(viewLifecycleOwner, Observer { currentIntake ->
            val targetIntake = homeViewModel.targetProteinIntake.value ?: 0 // 기본값을 0으로 설정

            if (currentIntake > targetIntake) {
                val progressDrawable = binding.proteinProgressBar.progressDrawable.mutate() as LayerDrawable
                val progressLayer = progressDrawable.findDrawableByLayerId(android.R.id.progress) as ClipDrawable
                progressLayer.setColorFilter(ContextCompat.getColor(requireContext(), R.color.progressbar_red), PorterDuff.Mode.SRC_IN)
                binding.proteinProgressBar.progressDrawable = progressDrawable
            } else {
                val progressDrawable = binding.proteinProgressBar.progressDrawable.mutate() as LayerDrawable
                val progressLayer = progressDrawable.findDrawableByLayerId(android.R.id.progress) as ClipDrawable
                progressLayer.setColorFilter(ContextCompat.getColor(requireContext(), R.color.progressbar_green), PorterDuff.Mode.SRC_IN)
                binding.proteinProgressBar.progressDrawable = progressDrawable
            }
        })

        homeViewModel.currentFatIntake.observe(viewLifecycleOwner, Observer { currentIntake ->
            val targetIntake = homeViewModel.targetFatIntake.value ?: 0

            if (currentIntake > targetIntake) {
                val progressDrawable = binding.fatProgressBar.progressDrawable.mutate() as LayerDrawable
                val progressLayer = progressDrawable.getDrawable(2) as ClipDrawable // findDrawableByLayerId 대신 getDrawable 사용
                progressLayer.setColorFilter(ContextCompat.getColor(requireContext(), R.color.progressbar_red), PorterDuff.Mode.SRC_IN)
                binding.fatProgressBar.progressDrawable = progressDrawable
            } else {
                val progressDrawable = binding.fatProgressBar.progressDrawable.mutate() as LayerDrawable
                val progressLayer = progressDrawable.getDrawable(2) as ClipDrawable // findDrawableByLayerId 대신 getDrawable 사용
                progressLayer.setColorFilter(ContextCompat.getColor(requireContext(), R.color.progressbar_green), PorterDuff.Mode.SRC_IN)
                binding.fatProgressBar.progressDrawable = progressDrawable
            }
        })
    }

    private fun navigateToMealDetail(mealType: String) {
        homeViewModel.setMealType(mealType)
        val action = HomeFragmentDirections.actionNavigationHomeToMealDetailFragment()
        findNavController().navigate(action)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
