package com.example.fitnutrijournal.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.databinding.NutritionProgressViewBinding
import com.example.fitnutrijournal.viewmodel.HomeViewModel

class NutritionProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: NutritionProgressViewBinding =
        NutritionProgressViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setViewModel(
        viewModel: HomeViewModel,
        targetCarbIntake: LiveData<Int>,
        currentCarbIntake: LiveData<Int>,
        targetProteinIntake: LiveData<Int>,
        currentProteinIntake: LiveData<Int>,
        targetFatIntake: LiveData<Int>,
        currentFatIntake: LiveData<Int>,
        targetCalories: LiveData<Int>,
        currentCalories: LiveData<Int>
    ) {
        binding.viewModel = viewModel
        binding.targetCarbIntake = targetCarbIntake
        binding.currentCarbIntake = currentCarbIntake
        binding.targetProteinIntake = targetProteinIntake
        binding.currentProteinIntake = currentProteinIntake
        binding.targetFatIntake = targetFatIntake
        binding.currentFatIntake = currentFatIntake
        binding.targetCalories = targetCalories
        binding.currentCalories = currentCalories
        binding.executePendingBindings()
    }
}