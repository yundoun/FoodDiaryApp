package com.example.fitnutrijournal.ui.home

import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDietDetailBinding.inflate(inflater, container, false)

        (activity as MainActivity).showBottomNavigation(false)

        dietViewModel.selectedDiet.observe(viewLifecycleOwner) { diet ->
            binding.foodName.text = diet?.foodName ?: ""
            binding.totalContent.text = diet?.totalContent?.toString() ?: ""
            binding.calories.text = diet?.calories?.toString() ?: ""
            binding.carbohydrate.text = diet?.carbohydrate?.toString() ?: ""
            binding.protein.text = diet?.protein?.toString() ?: ""
            binding.fat.text = diet?.fat?.toString() ?: ""

            binding.totalContentInput.setText(diet?.totalContent?.toString() ?: "")
            binding.unit.setText("g")
        }

        return binding.root
    }
}
