package com.example.fitnutrijournal.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReportViewModelFactory(
    private val application: Application,
    private val homeViewModel: HomeViewModel
) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(application, homeViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
