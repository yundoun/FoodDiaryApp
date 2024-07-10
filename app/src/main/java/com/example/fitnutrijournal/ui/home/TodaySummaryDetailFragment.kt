package com.example.fitnutrijournal.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.databinding.FragmentTodaySummaryDetailBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator

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

        (activity as MainActivity).showBottomNavigation(false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.testBtn.setOnClickListener{
            homeViewModel.setMaxCarbsBreakfast(500)
            homeViewModel.addCarbsBreakfast(100)
            Log.d("TodaySummaryDetailFragment", "Breakfast: ${homeViewModel.currentCarbIntakeBreakfast.value}")
        }

        return binding.root
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