package com.example.fitnutrijournal.ui.diet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.databinding.FragmentCustomAddBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.data.model.Food

class CustomAddFragment : Fragment() {

    private var _binding: FragmentCustomAddBinding? = null
    private val binding get() = _binding!!
    private val dietViewModel: DietViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomAddBinding.inflate(inflater, container, false).apply {
            viewModel = dietViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        (activity as MainActivity).showBottomNavigation(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addTextWatchers()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addBtn.setOnClickListener {
            val foodName = binding.inputName.text.toString()
            val servingSize = binding.inputServingSize.text.toString().toIntOrNull() ?: 0
            val calories = binding.inputCalories.text.toString().toFloatOrNull() ?: 0f
            val carbohydrate = binding.inputCarb.text.toString().toFloatOrNull() ?: 0f
            val protein = binding.inputProtein.text.toString().toFloatOrNull() ?: 0f
            val fat = binding.inputFat.text.toString().toFloatOrNull() ?: 0f

            if (foodName.isNotEmpty() && servingSize > 0) {
                val food = Food(
                    foodCd = "", // foodCd는 DietViewModel에서 생성
                    foodName = foodName,
                    servingSize = servingSize,
                    calories = calories,
                    carbohydrate = carbohydrate,
                    protein = protein,
                    fat = fat,
                    caloriesPerGram = calories / servingSize,
                    isFavorite = false,
                    isAddedByUser = true
                )
                dietViewModel.insertFood(food)
            }

            findNavController().popBackStack()
            Toast.makeText(context, "음식이 추가되었습니다.", Toast.LENGTH_SHORT).show()
        }

        binding.addBtn.isEnabled = false
    }

    private fun addTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkFieldsForEmptyValues()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.inputName.addTextChangedListener(textWatcher)
        binding.inputServingSize.addTextChangedListener(textWatcher)
        binding.inputCalories.addTextChangedListener(textWatcher)
        binding.inputCarb.addTextChangedListener(textWatcher)
        binding.inputProtein.addTextChangedListener(textWatcher)
        binding.inputFat.addTextChangedListener(textWatcher)
    }

    private fun checkFieldsForEmptyValues() {
        val name = binding.inputName.text.toString()
        val servingSize = binding.inputServingSize.text.toString()
        val calories = binding.inputCalories.text.toString()
        val carb = binding.inputCarb.text.toString()
        val protein = binding.inputProtein.text.toString()
        val fat = binding.inputFat.text.toString()

        binding.addBtn.isEnabled = name.isNotEmpty() && servingSize.isNotEmpty() &&
                calories.isNotEmpty() && carb.isNotEmpty() &&
                protein.isNotEmpty() && fat.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
