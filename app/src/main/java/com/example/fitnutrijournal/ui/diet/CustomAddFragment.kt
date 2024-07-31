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
            if (validateFields()) {
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
                    findNavController().popBackStack()
                    Toast.makeText(context, "음식이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "모든 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateFields()
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

    private fun validateFields(): Boolean {
        var isValid = true

        if (binding.inputName.text.toString().isEmpty()) {
            binding.inputName.error = "이름을 입력해주세요."
            isValid = false
        } else {
            binding.inputName.error = null
        }

        if (binding.inputServingSize.text.toString().isEmpty()) {
            binding.inputServingSize.error = "기본 제공량을 입력해주세요."
            isValid = false
        } else {
            binding.inputServingSize.error = null
        }

        if (binding.inputCalories.text.toString().isEmpty()) {
            binding.inputCalories.error = "열량을 입력해주세요."
            isValid = false
        } else {
            binding.inputCalories.error = null
        }

        if (binding.inputCarb.text.toString().isEmpty()) {
            binding.inputCarb.error = "탄수화물을 입력해주세요."
            isValid = false
        } else {
            binding.inputCarb.error = null
        }

        if (binding.inputProtein.text.toString().isEmpty()) {
            binding.inputProtein.error = "단백질을 입력해주세요."
            isValid = false
        } else {
            binding.inputProtein.error = null
        }

        if (binding.inputFat.text.toString().isEmpty()) {
            binding.inputFat.error = "지방을 입력해주세요."
            isValid = false
        } else {
            binding.inputFat.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
