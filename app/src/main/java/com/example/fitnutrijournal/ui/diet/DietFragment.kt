package com.example.fitnutrijournal.ui.diet

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fitnutrijournal.databinding.FragmentDietBinding
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.google.android.material.tabs.TabLayoutMediator

class DietFragment : Fragment() {

    private var _binding: FragmentDietBinding? = null
    private val binding get() = _binding!!
    private val dietViewModel: DietViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietBinding.inflate(inflater, container, false).apply {
            viewModel = dietViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        val viewPager = binding.viewPager
        val adapter = DietPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
