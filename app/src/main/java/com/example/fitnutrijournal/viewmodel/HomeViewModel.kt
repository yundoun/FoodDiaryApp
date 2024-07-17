package com.example.fitnutrijournal.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fitnutrijournal.data.database.FoodDatabase
import com.example.fitnutrijournal.data.model.DailyIntakeGoal
import com.example.fitnutrijournal.data.model.DailyIntakeRecord
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.data.model.Meal
import com.example.fitnutrijournal.data.repository.DailyIntakeGoalRepository
import com.example.fitnutrijournal.data.repository.DailyIntakeRecordRepository
import com.example.fitnutrijournal.data.repository.DietRepository
import com.example.fitnutrijournal.data.repository.MealRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val dietRepository: DietRepository
    private val mealRepository: MealRepository
    private val dailyIntakeGoalRepository: DailyIntakeGoalRepository
    private val dailyIntakeRecordRepository: DailyIntakeRecordRepository

    // 하루 섭취 목표
    private val _todayGoal = MutableLiveData<DailyIntakeGoal?>()
    val todayGoal: LiveData<DailyIntakeGoal?> get() = _todayGoal

    // 날짜
    private val _todayDate = MutableLiveData<String>().apply {
        value = LocalDate.now().format(dateFormatter)
    }
    private val todayDate: LiveData<String>
        get() = _todayDate

    // 현재 날짜 = 오늘 날짜 기본값
    private val _currentDate = MutableLiveData<String>().apply {
        value = todayDate.value
    }
    val currentDate: LiveData<String>
        get() = _currentDate

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String>
        get() = _selectedDate

    fun setCurrentDate(date: String) {
        _currentDate.value = date
    }

    // 하루 섭취 기록
    private val _dailyIntakeRecord = MutableLiveData<DailyIntakeRecord?>()
    val dailyIntakeRecord: LiveData<DailyIntakeRecord?> get() = _dailyIntakeRecord

    // 각 식사 유형별로 섭취한 영양소 데이터를 저장할 변수들
    private val _breakfastNutrients = MutableLiveData<NutrientData>()
    val breakfastNutrients: LiveData<NutrientData> get() = _breakfastNutrients

    private val _lunchNutrients = MutableLiveData<NutrientData>()
    val lunchNutrients: LiveData<NutrientData> get() = _lunchNutrients

    private val _dinnerNutrients = MutableLiveData<NutrientData>()
    val dinnerNutrients: LiveData<NutrientData> get() = _dinnerNutrients

    private val _snackNutrients = MutableLiveData<NutrientData>()
    val snackNutrients: LiveData<NutrientData> get() = _snackNutrients

    // 데이터를 저장할 클래스
    data class NutrientData(
        var calories: Int = 0,
        var carbs: Float = 0f,
        var protein: Float = 0f,
        var fat: Float = 0f,
        var quantity: Float = 0f
    )

    // checkedItems를 Meal 객체로 변환하고 DailyIntakeRecord와 각 식사 유형별 섭취한 영양소 데이터를 업데이트하는 메소드
    fun addCheckedItemsToDailyIntakeRecord(checkedItems: Set<Food>, date: String, mealType: String) {
        viewModelScope.launch {
            val meals = checkedItems.map { food ->
                Meal(
                    date = date,
                    mealType = mealType,
                    dietFoodCode = food.foodCd,
                    quantity = food.servingSize.toFloat()
                )
            }

            addMealsAndUpdateIntakeRecord(meals, mealType)
        }
    }

    private suspend fun addMealsAndUpdateIntakeRecord(meals: List<Meal>, mealType: String) {
        for (meal in meals) {
            // Meal 데이터를 추가
            mealRepository.insert(meal)

            // Food 데이터를 조회
            val food = dietRepository.getFoodByFoodCode(meal.dietFoodCode)

            // DailyIntakeRecord 데이터를 가져오기
            val dailyIntakeRecord = dailyIntakeRecordRepository.getRecordByDate(meal.date).value
                ?: DailyIntakeRecord(meal.date)

            // Food의 영양성분을 이용해 DailyIntakeRecord의 값 증가
            val updatedRecord = dailyIntakeRecord.copy(
                currentCalories = dailyIntakeRecord.currentCalories + (food.calories * meal.quantity / food.servingSize).toInt(),
                currentCarbs = dailyIntakeRecord.currentCarbs + (food.carbohydrate * meal.quantity / food.servingSize).toInt(),
                currentProtein = dailyIntakeRecord.currentProtein + (food.protein * meal.quantity / food.servingSize).toInt(),
                currentFat = dailyIntakeRecord.currentFat + (food.fat * meal.quantity / food.servingSize).toInt()
            )

            // DailyIntakeRecord 업데이트
            dailyIntakeRecordRepository.insert(updatedRecord)
            _dailyIntakeRecord.value = updatedRecord

            // 각 식사 유형별로 섭취한 영양소 데이터 업데이트
            updateNutrientData(mealType, food, meal.quantity)
        }
    }

    private fun updateNutrientData(mealType: String, food: Food, quantity: Float) {
        val nutrientData = when (mealType) {
            "breakfast" -> _breakfastNutrients.value ?: NutrientData()
            "lunch" -> _lunchNutrients.value ?: NutrientData()
            "dinner" -> _dinnerNutrients.value ?: NutrientData()
            "snack" -> _snackNutrients.value ?: NutrientData()
            else -> NutrientData()
        }

        nutrientData.apply {
            calories += (food.calories * quantity / food.servingSize).toInt()
            carbs += (food.carbohydrate * quantity / food.servingSize)
            protein += (food.protein * quantity / food.servingSize)
            fat += (food.fat * quantity / food.servingSize)
            this.quantity += quantity
        }

        when (mealType) {
            "breakfast" -> _breakfastNutrients.value = nutrientData
            "lunch" -> _lunchNutrients.value = nutrientData
            "dinner" -> _dinnerNutrients.value = nutrientData
            "snack" -> _snackNutrients.value = nutrientData
        }
    }

    // 현재 날짜에 대한 식사 기록을 불러오는 메소드
    fun loadDailyIntakeForDate(date: String) {
        viewModelScope.launch {
            val meals = mealRepository.getMealsByDate(date).value ?: return@launch
            meals.forEach { meal ->
                val food = dietRepository.getFoodByFoodCode(meal.dietFoodCode)
                updateNutrientData(meal.mealType, food, meal.quantity)
            }
        }
    }

    init {
        val database = FoodDatabase.getDatabase(application)
        val dailyIntakeGoalDao = database.dailyIntakeGoalDao()
        val dailyIntakeRecordDao = database.dailyIntakeRecordDao()
        val foodDao = database.foodDao()
        val mealDao = database.mealDao()

        dailyIntakeGoalRepository = DailyIntakeGoalRepository(dailyIntakeGoalDao)
        dailyIntakeRecordRepository = DailyIntakeRecordRepository(dailyIntakeRecordDao)
        dietRepository = DietRepository(foodDao)
        mealRepository = MealRepository(mealDao)

        // 현재 날짜의 섭취 목표를 로드합니다.
        loadDailyIntakeGoal(selectedDate.value ?: LocalDate.now().format(dateFormatter))

        // 데이터 로드
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        setCurrentDate(currentDate)
        loadDailyIntakeForDate(currentDate)

    }


    // 하루 섭취 목표 저장 ( 아침, 점심, 저녁, 간식 칼로리 + 탄단지 목표는 아직 구현 안함 )
    fun saveDailyIntakeGoal(
        date: String,
        targetCalories: Int,
        targetBreakfast: Int,
        targetLunch: Int,
        targetDinner: Int,
        targetSnack: Int
    ) {
        viewModelScope.launch {
            val goal = DailyIntakeGoal(
                date,
                targetCalories,
                targetBreakfast,
                targetLunch,
                targetDinner,
                targetSnack
            )
            dailyIntakeGoalRepository.insertOrUpdate(goal)
            Log.d("HomeViewModel", "Daily intake goal saved: $goal")
            _todayGoal.value = goal // 저장된 목표를 LiveData에 반영합니다.
        }
    }

    // 하루 섭취 목표 가져오기
    private fun loadDailyIntakeGoal(date: String) {
        viewModelScope.launch {
            dailyIntakeGoalRepository.getDailyIntakeGoal(date).observeForever { goal ->
                if (goal == null) {
                    // 기본값 설정
                    val defaultGoal = DailyIntakeGoal(
                        date = date,
                        targetCalories = 0,
                        targetBreakfast = 0,
                        targetLunch = 0,
                        targetDinner = 0,
                        targetSnack = 0
                    )
                    _todayGoal.value = defaultGoal
                } else {
                    _todayGoal.value = goal
                }
            }
        }
    }

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
