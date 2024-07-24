package com.example.fitnutrijournal.data.repository

import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.MealDao
import com.example.fitnutrijournal.data.model.Meal

class MealRepository(private val mealDao: MealDao) {

    // 주어진 Meal 객체를 삽입하는 역할
    suspend fun insert(meal: Meal) {
        mealDao.insert(meal)
    }

    // 특정 날짜(date)에 해당하는 Meal 객체 리스트를 LiveData 형태로 반환
    fun getMealsByDate(date: String): LiveData<List<Meal>> {
        return mealDao.getMealsByDate(date)
    }

    // 특정 날짜(date)에 해당하는 Meal 객체 리스트를 반환
    suspend fun getMealsByDateAndTypeSync(date: String, mealType: String): List<Meal> {
        return mealDao.getMealsByDateAndTypeSync(date, mealType)
    }

    // 특정 Meal 삭제
    suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal.date, meal.mealType, meal.dietFoodCode)
    }

    suspend fun deleteMealById(id: Long) {
        mealDao.deleteMealById(id)
    }

    // 특정 식품 코드에 해당하는 Meal 객체 리스트를 반환
    suspend fun getMealsByFoodCode(foodCode: String): List<Meal> {
        return mealDao.getMealsByFoodCode(foodCode)
    }
}
