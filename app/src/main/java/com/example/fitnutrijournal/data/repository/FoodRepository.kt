package com.example.fitnutrijournal.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.FoodDao
import com.example.fitnutrijournal.data.model.Food

class FoodRepository(private val foodDao: FoodDao) {
    val allFoods: LiveData<List<Food>> = foodDao.getAllFoods()
    val favoriteFoods: LiveData<List<Food>> = foodDao.getFavoriteFoods()
    val userAddedFoods: LiveData<List<Food>> = foodDao.getUserAddedFoods()

    // 주어진 Food 객체를 삽입
    suspend fun insert(food: Food) {
        foodDao.insert(food)
        Log.d("DietRepository", "Food inserted: $food")
    }

    // 주어진 Food 객체를 업데이트
    suspend fun update(food: Food) {
        foodDao.update(food)
        Log.d("DietRepository", "Food updated: $food")
    }

    // 주어진 Food 리스트를 삽입
    private suspend fun insertAll(foods: List<Food>) {
        foodDao.insertAll(foods)
        Log.d("DietRepository", "Foods inserted: $foods")
    }

    // 특정 foodCode에 해당하는 Food 객체를 반환
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

    suspend fun getMaxFoodCd(): String? {
        return foodDao.getMaxFoodCd()
    }

    // Food 데이터 삭제
    suspend fun delete(food: Food) {
        foodDao.delete(food)
    }
}
