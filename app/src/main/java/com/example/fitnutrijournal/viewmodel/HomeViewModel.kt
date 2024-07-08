package com.example.fitnutrijournal.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel : ViewModel() {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")


    private val _todayDate = MutableLiveData<String>().apply {
        value = LocalDate.now().format(dateFormatter)
    }
    val todayDate: LiveData<String>
        get() = _todayDate

    private val _currentDate = MutableLiveData<String>().apply {
        value = _todayDate.value
    }
    val currentDate: LiveData<String>
        get() = _currentDate

    fun updateCurrentDate(date: LocalDate) {
        val newDate = date.format(dateFormatter)
        if (_currentDate.value != newDate) {
            _currentDate.value = newDate
            Log.d("HomeViewModel", "Current date updated to: $newDate")
        }
    }
}
