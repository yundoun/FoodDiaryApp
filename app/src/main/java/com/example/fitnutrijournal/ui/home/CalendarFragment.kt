// CalendarFragment.kt
package com.example.fitnutrijournal.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.fitnutrijournal.databinding.FragmentCalendarBinding
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import java.util.*

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setupViewPager(binding.viewPager)

        binding.selectDate.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }


        return binding.root
    }

    private fun setupViewPager(viewPager: ViewPager2) {
        val adapter = CalendarPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(adapter.itemCount / 2, false) // 현재 달을 중앙으로 설정
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
