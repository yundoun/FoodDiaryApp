package com.example.fitnutrijournal.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fitnutrijournal.databinding.FragmentDietDetailBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel

class DietDetailFragment : Fragment() {

    private val dietViewModel: DietViewModel by activityViewModels()
    private lateinit var binding: FragmentDietDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDietDetailBinding.inflate(inflater, container, false)
        binding.viewModel = dietViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        (activity as MainActivity).showBottomNavigation(false)

        dietViewModel.selectedDiet.observe(viewLifecycleOwner) { diet ->
            binding.totalContentInput.setText(diet?.servingSize?.toString() ?: "")
            binding.unit.setText("g")
        }

        binding.totalContentInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dietViewModel.updateTotalContent(s.toString())
            }
        })


        return binding.root
    }
}
