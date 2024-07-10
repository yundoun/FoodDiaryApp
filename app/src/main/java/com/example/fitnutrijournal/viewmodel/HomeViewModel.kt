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

    // 오늘 하루 Total
    private val _targetCarbIntake = MutableLiveData(0)
    private val _currentCarbIntake = MutableLiveData(0)
    private val _targetProteinIntake = MutableLiveData(0)
    private val _currentProteinIntake = MutableLiveData(0)
    private val _targetFatIntake = MutableLiveData(0)
    private val _currentFatIntake = MutableLiveData(0)
    private val _targetCalories = MutableLiveData(0)
    private val _currentCalories = MutableLiveData(0)
    private val _remainingCalories = MediatorLiveData<Int>()

    // 아침
    private val _targetCarbIntakeBreakfast = MutableLiveData(0)
    private val _currentCarbIntakeBreakfast = MutableLiveData(0)
    private val _targetProteinIntakeBreakfast = MutableLiveData(0)
    private val _currentProteinIntakeBreakfast = MutableLiveData(0)
    private val _targetFatIntakeBreakfast = MutableLiveData(0)
    private val _currentFatIntakeBreakfast = MutableLiveData(0)
    private val _targetCaloriesBreakfast = MutableLiveData(0)
    private val _currentCaloriesBreakfast = MutableLiveData(0)
    private val _remainingCaloriesBreakfast = MediatorLiveData<Int>()

    // 점심
    private val _targetCarbIntakeLunch = MutableLiveData(0)
    private val _currentCarbIntakeLunch = MutableLiveData(0)
    private val _targetProteinIntakeLunch = MutableLiveData(0)
    private val _currentProteinIntakeLunch = MutableLiveData(0)
    private val _targetFatIntakeLunch = MutableLiveData(0)
    private val _currentFatIntakeLunch = MutableLiveData(0)
    private val _targetCaloriesLunch = MutableLiveData(0)
    private val _currentCaloriesLunch = MutableLiveData(0)
    private val _remainingCaloriesLunch = MediatorLiveData<Int>()

    // 저녁
    private val _targetCarbIntakeDinner = MutableLiveData(0)
    private val _currentCarbIntakeDinner = MutableLiveData(0)
    private val _targetProteinIntakeDinner = MutableLiveData(0)
    private val _currentProteinIntakeDinner = MutableLiveData(0)
    private val _targetFatIntakeDinner = MutableLiveData(0)
    private val _currentFatIntakeDinner = MutableLiveData(0)
    private val _targetCaloriesDinner = MutableLiveData(0)
    private val _currentCaloriesDinner = MutableLiveData(0)
    private val _remainingCaloriesDinner = MediatorLiveData<Int>()

    // 간식
    private val _targetCarbIntakeSnack = MutableLiveData(0)
    private val _currentCarbIntakeSnack = MutableLiveData(0)
    private val _targetProteinIntakeSnack = MutableLiveData(0)
    private val _currentProteinIntakeSnack = MutableLiveData(0)
    private val _targetFatIntakeSnack = MutableLiveData(0)
    private val _currentFatIntakeSnack = MutableLiveData(0)
    private val _targetCaloriesSnack = MutableLiveData(0)
    private val _currentCaloriesSnack = MutableLiveData(0)
    private val _remainingCaloriesSnack = MediatorLiveData<Int>()

    // 오늘 하루 Total getter
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
    val remainingCalories: LiveData<Int>
        get() = _remainingCalories

    // 아침 getter
    val targetCarbIntakeBreakfast: LiveData<Int>
        get() = _targetCarbIntakeBreakfast
    val currentCarbIntakeBreakfast: LiveData<Int>
        get() = _currentCarbIntakeBreakfast
    val targetProteinIntakeBreakfast: LiveData<Int>
        get() = _targetProteinIntakeBreakfast
    val currentProteinIntakeBreakfast: LiveData<Int>
        get() = _currentProteinIntakeBreakfast
    val targetFatIntakeBreakfast: LiveData<Int>
        get() = _targetFatIntakeBreakfast
    val currentFatIntakeBreakfast: LiveData<Int>
        get() = _currentFatIntakeBreakfast
    val targetCaloriesBreakfast: LiveData<Int>
        get() = _targetCaloriesBreakfast
    val currentCaloriesBreakfast: LiveData<Int>
        get() = _currentCaloriesBreakfast

    // 점심 getter
    val targetCarbIntakeLunch: LiveData<Int>
        get() = _targetCarbIntakeLunch
    val currentCarbIntakeLunch: LiveData<Int>
        get() = _currentCarbIntakeLunch
    val targetProteinIntakeLunch: LiveData<Int>
        get() = _targetProteinIntakeLunch
    val currentProteinIntakeLunch: LiveData<Int>
        get() = _currentProteinIntakeLunch
    val targetFatIntakeLunch: LiveData<Int>
        get() = _targetFatIntakeLunch
    val currentFatIntakeLunch: LiveData<Int>
        get() = _currentFatIntakeLunch
    val targetCaloriesLunch: LiveData<Int>
        get() = _targetCaloriesLunch
    val currentCaloriesLunch: LiveData<Int>
        get() = _currentCaloriesLunch

    // 저녁 getter
    val targetCarbIntakeDinner: LiveData<Int>
        get() = _targetCarbIntakeDinner
    val currentCarbIntakeDinner: LiveData<Int>
        get() = _currentCarbIntakeDinner
    val targetProteinIntakeDinner: LiveData<Int>
        get() = _targetProteinIntakeDinner
    val currentProteinIntakeDinner: LiveData<Int>
        get() = _currentProteinIntakeDinner
    val targetFatIntakeDinner: LiveData<Int>
        get() = _targetFatIntakeDinner
    val currentFatIntakeDinner: LiveData<Int>
        get() = _currentFatIntakeDinner
    val targetCaloriesDinner: LiveData<Int>
        get() = _targetCaloriesDinner
    val currentCaloriesDinner: LiveData<Int>
        get() = _currentCaloriesDinner

    // 간식 getter
    val targetCarbIntakeSnack: LiveData<Int>
        get() = _targetCarbIntakeSnack
    val currentCarbIntakeSnack: LiveData<Int>
        get() = _currentCarbIntakeSnack
    val targetProteinIntakeSnack: LiveData<Int>
        get() = _targetProteinIntakeSnack
    val currentProteinIntakeSnack: LiveData<Int>
        get() = _currentProteinIntakeSnack
    val targetFatIntakeSnack: LiveData<Int>
        get() = _targetFatIntakeSnack
    val currentFatIntakeSnack: LiveData<Int>
        get() = _currentFatIntakeSnack
    val targetCaloriesSnack: LiveData<Int>
        get() = _targetCaloriesSnack
    val currentCaloriesSnack: LiveData<Int>
        get() = _currentCaloriesSnack


    // 잔여 칼로리 계산
    init {
        _remainingCalories.addSource(_currentCalories) { updateRemainingCalories() }
        _remainingCalories.addSource(_targetCalories) { updateRemainingCalories() }
    }

    private fun updateRemainingCalories() {
        val max = _targetCalories.value ?: 0
        val current = _currentCalories.value ?: 0
        _remainingCalories.value = max - current
    }


    // 날짜
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

    // 섭취량 증가시키는 테스트 코드
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

    fun addCarbsBreakfast(carbs: Int) {
        _currentCarbIntakeBreakfast.value = (_currentCarbIntakeBreakfast.value ?: 0) + carbs
        if (_currentCarbIntakeBreakfast.value!! > _targetCarbIntakeBreakfast.value!!) {
            _currentCarbIntakeBreakfast.value = _targetCarbIntakeBreakfast.value
        }
    }

    fun addProteinBreakfast(protein: Int) {
        _currentProteinIntakeBreakfast.value = (_currentProteinIntakeBreakfast.value ?: 0) + protein
        if (_currentProteinIntakeBreakfast.value!! > _targetProteinIntakeBreakfast.value!!) {
            _currentProteinIntakeBreakfast.value = _targetProteinIntakeBreakfast.value
        }
    }

    fun addFatBreakfast(fat: Int) {
        _currentFatIntakeBreakfast.value = (_currentFatIntakeBreakfast.value ?: 0) + fat
        if (_currentFatIntakeBreakfast.value!! > _targetFatIntakeBreakfast.value!!) {
            _currentFatIntakeBreakfast.value = _targetFatIntakeBreakfast.value
        }
    }

    fun addCaloriesBreakfast(calories: Int) {
        _currentCaloriesBreakfast.value = (_currentCaloriesBreakfast.value ?: 0) + calories
        if (_currentCaloriesBreakfast.value!! > _targetCaloriesBreakfast.value!!) {
            _currentCaloriesBreakfast.value = _targetCaloriesBreakfast.value
        }
    }

    fun addCarbsLunch(carbs: Int) {
        _currentCarbIntakeLunch.value = (_currentCarbIntakeLunch.value ?: 0) + carbs
        if (_currentCarbIntakeLunch.value!! > _targetCarbIntakeLunch.value!!) {
            _currentCarbIntakeLunch.value = _targetCarbIntakeLunch.value
        }
    }

    fun addProteinLunch(protein: Int) {
        _currentProteinIntakeLunch.value = (_currentProteinIntakeLunch.value ?: 0) + protein
        if (_currentProteinIntakeLunch.value!! > _targetProteinIntakeLunch.value!!) {
            _currentProteinIntakeLunch.value = _targetProteinIntakeLunch.value
        }
    }

    fun addFatLunch(fat: Int) {
        _currentFatIntakeLunch.value = (_currentFatIntakeLunch.value ?: 0) + fat
        if (_currentFatIntakeLunch.value!! > _targetFatIntakeLunch.value!!) {
            _currentFatIntakeLunch.value = _targetFatIntakeLunch.value
        }
    }

    fun addCaloriesLunch(calories: Int) {
        _currentCaloriesLunch.value = (_currentCaloriesLunch.value ?: 0) + calories
        if (_currentCaloriesLunch.value!! > _targetCaloriesLunch.value!!) {
            _currentCaloriesLunch.value = _targetCaloriesLunch.value
        }
    }

    // 목표값을 설정하는 테스트 코드
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

    fun setMaxCarbsBreakfast(max: Int) {
        _targetCarbIntakeBreakfast.value = max
    }

    fun setMaxProteinBreakfast(max: Int) {
        _targetProteinIntakeBreakfast.value = max
    }

    fun setMaxFatBreakfast(max: Int) {
        _targetFatIntakeBreakfast.value = max
    }

    fun setMaxCaloriesBreakfast(max: Int) {
        _targetCaloriesBreakfast.value = max
    }

    fun setMaxCarbsLunch(max: Int) {
        _targetCarbIntakeLunch.value = max
    }

    fun setMaxProteinLunch(max: Int) {
        _targetProteinIntakeLunch.value = max
    }

    fun setMaxFatLunch(max: Int) {
        _targetFatIntakeLunch.value = max
    }

    fun setMaxCaloriesLunch(max: Int) {
        _targetCaloriesLunch.value = max
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
