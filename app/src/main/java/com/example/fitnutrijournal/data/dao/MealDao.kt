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
}
