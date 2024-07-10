package com.example.fitnutrijournal.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel : ViewModel() {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _targetCarbIntake = MutableLiveData(0)
    private val _currentCarbIntake = MutableLiveData(0)
    private val _targetProteinIntake = MutableLiveData(0)
    private val _currentProteinIntake = MutableLiveData(0)
    private val _targetFatIntake = MutableLiveData(0)
    private val _currentFatIntake = MutableLiveData(0)
    private val _targetCalories = MutableLiveData(0)
    private val _currentCalories = MutableLiveData(0)
    private val _remainingCalories = MutableLiveData(0)

    val targetCarbIntake: LiveData<Int>
        get() = _targetCarbIntake
    val currentCarbIntake: LiveData<Int>
        get() = _currentCarbIntake
    val targetProteinIntake: LiveData<Int>
        get() = _targetProteinIntake
    val currentProteinIntake: LiveData<Int>
        get() = _currentProteinIntake
    val targetFatIntake: LiveData<Int>
        get() = _targetFatIntake
    val currentFatIntake: LiveData<Int>
        get() = _currentFatIntake
    val targetCalories: LiveData<Int>
        get() = _targetCalories
    val currentCalories: LiveData<Int>
        get() = _currentCalories
    val remainingCalories = MediatorLiveData<Int>().apply {
        addSource(_currentCalories) { updateRemainingCalories() }
        addSource(_targetCalories) { updateRemainingCalories() }
    }


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

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String>
        get() = _selectedDate

    fun setCarbIntake(intake: Int) {
        _currentCarbIntake.value = intake
    }

    fun setTargetCarbIntake(intake: Int) {
        _targetCarbIntake.value = intake
    }

    private fun updateRemainingCalories() {
        val max = _targetCalories.value ?: 0
        val current = _currentCalories.value ?: 0
        remainingCalories.value = max - current
    }


    // 탄수화물 섭취량을 증가시키는 테스트 함수
    fun addCarbs(carbs: Int) {
        _currentCarbIntake.value = (_currentCarbIntake.value ?: 0) + carbs
        if (_currentCarbIntake.value!! > _targetCarbIntake.value!!) {
            _currentCarbIntake.value = _targetCarbIntake.value
        }
    }

    fun addProtein(protein: Int) {
        _currentProteinIntake.value = (_currentProteinIntake.value ?: 0) + protein
        if (_currentProteinIntake.value!! > _targetProteinIntake.value!!) {
            _currentProteinIntake.value = _targetProteinIntake.value
        }
    }

    fun addFat(fat: Int) {
        _currentFatIntake.value = (_currentFatIntake.value ?: 0) + fat
        if (_currentFatIntake.value!! > _targetFatIntake.value!!) {
            _currentFatIntake.value = _targetFatIntake.value
        }
    }

    fun addCalories(calories: Int) {
        _currentCalories.value = (_currentCalories.value ?: 0) + calories
        if (_currentCalories.value!! > _targetCalories.value!!) {
            _currentCalories.value = _targetCalories.value
        }
    }

    // 목표 탄수화물 값을 임의로 설정하는 메소드
    fun setMaxCarbs(max: Int) {
        _targetCarbIntake.value = max
    }

    fun setMaxProtein(max: Int) {
        _targetProteinIntake.value = max
    }

    fun setMaxFat(max: Int) {
        _targetFatIntake.value = max
    }

    fun setMaxCalories(max: Int) {
        _targetCalories.value = max
    }


    fun updateCurrentDate(date: LocalDate) {
        val newDate = date.format(dateFormatter)
        if (_currentDate.value != newDate) {
            _currentDate.value = newDate
            Log.d("HomeViewModel", "Current date updated to: $newDate")
        }
    }

    fun updateSelectedDate(date: LocalDate) {
        val newDate = date.format(dateFormatter)
        if (_selectedDate.value != newDate) {
            _selectedDate.value = newDate
            Log.d("HomeViewModel", "Selected date updated to: $newDate")
        }
    }


}
