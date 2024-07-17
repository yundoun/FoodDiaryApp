package com.example.fitnutrijournal.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.FoodDao
import com.example.fitnutrijournal.data.model.Food

class DietRepository(private val foodDao: FoodDao) {
    val allFoods: LiveData<List<Food>> = foodDao.getAllFoods()
    val favoriteFoods: LiveData<List<Food>> = foodDao.getFavoriteFoods()
    val userAddedFoods: LiveData<List<Food>> = foodDao.getUserAddedFoods()

    suspend fun insert(food: Food) {
        foodDao.insert(food)
        Log.d("DietRepository", "Food inserted: $food")
    }

    suspend fun update(food: Food) {
        foodDao.update(food)
        Log.d("DietRepository", "Food updated: $food")
    }

    private suspend fun insertAll(foods: List<Food>) {
        foodDao.insertAll(foods)
        Log.d("DietRepository", "Foods inserted: $foods")
    }

    suspend fun getFoodByFoodCode(foodCode: String): Food {
        return foodDao.getFoodByFoodCode(foodCode)
    }

    suspend fun mergeAndInsertAll(apiFoods: List<Food>) {
        val currentFoods = foodDao.getAllFoodsList() // 모든 데이터를 리스트 형태로 가져오기 위한 메소드 필요
        val mergedFoods = apiFoods.map { apiFood ->
            currentFoods.find { it.foodCd == apiFood.foodCd }?.let { currentFood ->
                apiFood.copy(
                    isFavorite = currentFood.isFavorite,
                    isAddedByUser = currentFood.isAddedByUser
                )
            } ?: apiFood
        }
        insertAll(mergedFoods)
    }
}
