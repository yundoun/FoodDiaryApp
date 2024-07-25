package com.example.fitnutrijournal.ui.diet

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fitnutrijournal.databinding.FragmentFoodDetailBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.HomeViewModel

class FoodDetailFragment : Fragment() {

    private val dietViewModel: DietViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentFoodDetailBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        binding.viewModel = dietViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        (activity as MainActivity).showBottomNavigation(false)

        dietViewModel.selectedFood.observe(viewLifecycleOwner) { food ->
            val selectedMealQuantity = dietViewModel.selectedMealQuantity.value
            if (selectedMealQuantity != null) {
                binding.totalContentInput.setText(selectedMealQuantity.toString())
            } else {
                binding.totalContentInput.setText(food?.servingSize?.toString() ?: "")
            }
            binding.unit.setText("g")
        }

        binding.totalContentInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dietViewModel.updateTotalContent(s.toString())
            }
        })

        binding.btnSave.setOnClickListener {
            dietViewModel.saveCurrentFoodIntake()
            Toast.makeText(context, "식사가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        return binding.root
    }
}
