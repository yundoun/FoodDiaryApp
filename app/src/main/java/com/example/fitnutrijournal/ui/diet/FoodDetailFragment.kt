package com.example.fitnutrijournal.ui.diet

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.databinding.FragmentFoodDetailBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel

@RequiresApi(Build.VERSION_CODES.O)
class FoodDetailFragment : Fragment() {

    private val dietViewModel: DietViewModel by activityViewModels()
    private val homeViewModel: DietViewModel by activityViewModels()
    private lateinit var binding: FragmentFoodDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        binding.viewModel = dietViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        (activity as MainActivity).showBottomNavigation(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUpdate.setOnClickListener {
            dietViewModel.updateFoodIntake()
            Toast.makeText(context, "식사가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        dietViewModel.selectedFood.observe(viewLifecycleOwner) { food ->
            val selectedMealQuantity = dietViewModel.selectedMealQuantity.value
            if (selectedMealQuantity != null) {
                binding.totalContentInput.setText(selectedMealQuantity.toString())
                Log.d("FoodDetailFragment", "selectedMealQuantity: $selectedMealQuantity")
//                binding.calories.text = String.format("%.2f kcal", selectedMealQuantity * food.caloriesPerGram)
//                binding.carbohydrate.text = String.format("%.2f g", selectedMealQuantity * food.carbohydrate / food.servingSize)
//                binding.protein.text = String.format("%.2f g", selectedMealQuantity * food.protein / food.servingSize)
//                binding.fat.text = String.format("%.2f g", selectedMealQuantity * food.fat / food.servingSize)

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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        dietViewModel.clearSelectedMealQuantity()
    }

}
