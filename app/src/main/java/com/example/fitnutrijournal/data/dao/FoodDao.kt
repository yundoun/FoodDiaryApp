package com.example.fitnutrijournal.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitnutrijournal.data.model.Food

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: Food)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<Food>)

    @Update
    suspend fun update(food: Food)

    @Query("SELECT * FROM food_table")
    fun getAllFoods(): LiveData<List<Food>>

    @Query("SELECT * FROM food_table WHERE isFavorite = 1")
    fun getFavoriteFoods(): LiveData<List<Food>>

    @Query("SELECT * FROM food_table WHERE isAddedByUser = 1")
    fun getUserAddedFoods(): LiveData<List<Food>>

    @Query("SELECT * FROM food_table WHERE foodCd = :foodCode")
    suspend fun getFoodByFoodCode(foodCode: String): Food

    @Query("SELECT * FROM food_table")
    suspend fun getAllFoodsList(): List<Food>
}
