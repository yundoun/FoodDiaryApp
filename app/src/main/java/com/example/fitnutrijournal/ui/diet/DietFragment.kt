package com.example.fitnutrijournal.ui.diet

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.databinding.FragmentDietBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.google.android.material.tabs.TabLayoutMediator

class DietFragment : Fragment() {

    private var _binding: FragmentDietBinding? = null
    private val binding get() = _binding!!
    private val dietViewModel: DietViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietBinding.inflate(inflater, container, false).apply {
            viewModel = dietViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        (activity as MainActivity).showBottomNavigation(true)

        val viewPager = binding.viewPager
        val adapter = DietPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        // 실시간 검색을 위한 EditText의 TextWatcher 설정
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                Log.d("DietFragment", "Search query: $query")
                dietViewModel.setSearchQuery(query)
            }
        })

        binding.btnBack.setOnClickListener {
           findNavController().popBackStack()
        }

        handleArgs(arguments?.getString("source") ?: "")

        return binding.root
    }

    private fun handleArgs(source: String) {
        when (source) {
            "home" -> {
                // 음식 추가 관련 UI 조정
                (activity as MainActivity).showBottomNavigation(false)
                binding.btnAddFood.text = "선택된 1개 추가"

                val layoutParams = binding.btnAddFood.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = 50
                binding.btnAddFood.layoutParams = layoutParams

                dietViewModel.setCheckboxVisible(true) // 체크박스 표시 + 어댑터에서 즐겨찾기 숨김

            }
            else -> {
                // 네비게이션 바를 통해 접근했을 때 기본 UI
                dietViewModel.setCheckboxVisible(false) // 체크박스 숨김
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
