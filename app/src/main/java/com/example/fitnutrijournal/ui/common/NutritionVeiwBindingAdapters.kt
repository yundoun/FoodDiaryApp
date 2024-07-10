package com.example.fitnutrijournal.ui.common

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.viewmodel.HomeViewModel

@BindingAdapter(
    "viewModel",
    "targetCarbIntake",
    "currentCarbIntake",
    "targetProteinIntake",
    "currentProteinIntake",
    "targetFatIntake",
    "currentFatIntake",
    "targetCalories",
    "currentCalories"
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
    view.setViewModel(
        viewModel,
        targetCarbIntake,
        currentCarbIntake,
        targetProteinIntake,
        currentProteinIntake,
        targetFatIntake,
        currentFatIntake,
        targetCalories,
        currentCalories
    )
}
