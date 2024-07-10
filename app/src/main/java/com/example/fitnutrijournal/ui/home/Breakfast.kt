package com.example.fitnutrijournal.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fitnutrijournal.databinding.FragmentBreakfastBinding
import com.example.fitnutrijournal.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
class Breakfast : Fragment() {

    private var _binding: FragmentBreakfastBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreakfastBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }


        // 아침에 해당하는 변수 설정
        binding.nutritionProgressView.setViewModel(
            homeViewModel,
            homeViewModel.targetCarbIntakeBreakfast,
            homeViewModel.currentCarbIntakeBreakfast,
            homeViewModel.targetProteinIntakeBreakfast,
            homeViewModel.currentProteinIntakeBreakfast,
            homeViewModel.targetFatIntakeBreakfast,
            homeViewModel.currentFatIntakeBreakfast,
            homeViewModel.targetCaloriesBreakfast,
            homeViewModel.currentCaloriesBreakfast
        )




        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}