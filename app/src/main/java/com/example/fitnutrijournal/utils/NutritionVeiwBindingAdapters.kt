package com.example.fitnutrijournal.utils

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.viewmodel.HomeViewModel

@BindingAdapter(
    value = ["viewModel", "targetCarbIntake", "currentCarbIntake", "targetProteinIntake",
        "currentProteinIntake", "targetFatIntake", "currentFatIntake", "targetCalories", "currentCalories"],
    requireAll = true
)
fun setViewModel(
    view: NutritionProgressView,
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
    val binding = view.binding
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
