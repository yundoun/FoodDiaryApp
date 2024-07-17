package com.example.fitnutrijournal.data.repository

import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.MealDao
import com.example.fitnutrijournal.data.model.Meal

class MealRepository(private val mealDao: MealDao) {

    suspend fun insert(meal: Meal) {
        mealDao.insert(meal)
    }

    fun getMealsByDate(date: String): LiveData<List<Meal>> {
        return mealDao.getMealsByDate(date)
    }
}
