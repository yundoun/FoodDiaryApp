package com.example.fitnutrijournal.ui.home

import android.os.Build
import android.os.Bundle
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
import com.example.fitnutrijournal.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

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


        binding.testBtn.setOnClickListener {
            // 테스트 코드
            homeViewModel.setMaxCarbs(300) // 예시로 300g 설정
            homeViewModel.setMaxProtein(500)
            homeViewModel.setMaxFat(100)
            homeViewModel.setMaxCalories(2000)
            homeViewModel.addCarbs(50)
            homeViewModel.addProtein(30)
            homeViewModel.addFat(10)
            homeViewModel.addCalories(100)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
