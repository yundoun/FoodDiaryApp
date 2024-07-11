package com.example.fitnutrijournal.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitnutrijournal.data.model.Diet

@Dao
interface DietDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diet: Diet)

    @Update
    suspend fun update(diet: Diet)

    @Query("SELECT * FROM diet_table")
    fun getAllDiets(): LiveData<List<Diet>>

    @Query("SELECT * FROM diet_table WHERE isFavorite = 1")
    fun getFavoriteDiets(): LiveData<List<Diet>>
}
