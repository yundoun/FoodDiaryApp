package com.example.fitnutrijournal.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnutrijournal.data.model.Meal

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: Meal)

    @Query("SELECT * FROM Meal WHERE date = :date")
    fun getMealsByDate(date: String): LiveData<List<Meal>>

    @Query("SELECT * FROM Meal WHERE date = :date AND mealType = :mealType")
    suspend fun getMealsByDateAndTypeSync(date: String, mealType: String): List<Meal>

    @Query("SELECT * FROM Meal WHERE dietFoodCode = :foodCode")
    suspend fun getMealsByFoodCode(foodCode: String): List<Meal>

    // 특정 Meal 삭제
    @Query("DELETE FROM Meal WHERE date = :date AND mealType = :mealType AND dietFoodCode = :dietFoodCode")
    suspend fun deleteMeal(date: String, mealType: String, dietFoodCode: String)

    @Query("DELETE FROM Meal WHERE id = :id")
    suspend fun deleteMealById(id: Long)

    // 특정 날짜와 식사 유형에 해당하는 모든 Meal 삭제
    @Query("DELETE FROM Meal WHERE date = :date AND mealType = :mealType")
    suspend fun deleteMealsByDateAndType(date: String, mealType: String)
}
