package com.example.fitnutrijournal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {

    // 날짜 포맷 정의
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    // 현재 날짜 값을 설정하는 MutableLiveData
    private val _date = MutableLiveData<String>().apply {
        val currentDate = Calendar.getInstance().time
        value = dateFormat.format(currentDate)
    }
    val date: MutableLiveData<String>
        get() = _date
}