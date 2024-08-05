package com.example.fitnutrijournal.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.fitnutrijournal.data.database.FoodDatabase
import com.example.fitnutrijournal.data.repository.FoodRepository
import com.example.fitnutrijournal.data.repository.MealRepository
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class ReportViewModel(application: Application, private val homeViewModel: HomeViewModel) : AndroidViewModel(application) {
    private val mealRepository: MealRepository
    private val foodRepository: FoodRepository

    private val _caloriesByMealType = MediatorLiveData<Map<String, Int>>()
    val caloriesByMealType: LiveData<Map<String, Int>> get() = _caloriesByMealType

    init {
        val foodDatabase = FoodDatabase.getDatabase(application)
        mealRepository = MealRepository(foodDatabase.mealDao())
        foodRepository = FoodRepository(foodDatabase.foodDao())

        _caloriesByMealType.addSource(homeViewModel.currentDate) { date ->
            loadCaloriesByMealType(date)
            Log.d("ReportViewModel", "Current date changed to: $date")
        }
    }

    private fun loadCaloriesByMealType(date: String) {
        viewModelScope.launch {
            val mealList = mealRepository.getMealsByDateSync(date)
            if (mealList.isEmpty()) {
                _caloriesByMealType.postValue(emptyMap())
            } else {
                val mealCalories = mealList.groupBy { it.mealType }.mapValues { entry ->
                    entry.value.sumBy { meal ->
                        val food = foodRepository.getFoodByFoodCode(meal.dietFoodCode)
                        (food.calories * meal.quantity / food.servingSize).toInt()
                    }
                }
                _caloriesByMealType.postValue(mealCalories)
            }
        }
    }
}
