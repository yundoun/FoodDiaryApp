package com.example.fitnutrijournal.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fitnutrijournal.data.model.Photo

@Dao
interface PhotoDao {
    @Insert
    suspend fun insert(photo: Photo): Long

    @Query("SELECT * FROM photo WHERE date = :date AND mealType = :mealType")
    fun getPhotoByDateAndMealType(date: String, mealType: String): LiveData<Photo?>

    @Query("DELETE FROM photo WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: Long)
}