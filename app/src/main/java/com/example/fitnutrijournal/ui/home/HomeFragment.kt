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
