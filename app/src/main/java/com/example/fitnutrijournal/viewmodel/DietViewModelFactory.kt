package com.example.fitnutrijournal.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DietViewModelFactory(
    private val application: Application,
    private val homeViewModel: HomeViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DietViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DietViewModel(application, homeViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
