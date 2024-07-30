package com.example.fitnutrijournal.data.repository

import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.PhotoDao
import com.example.fitnutrijournal.data.model.Photo

class PhotoRepository(private val photoDao: PhotoDao) {

    suspend fun insert(photo: Photo) {
        photoDao.insert(photo)
    }

    fun getPhotoByDateAndMealType(date: String, mealType: String): LiveData<Photo?> {
        return photoDao.getPhotoByDateAndMealType(date, mealType)
    }

    suspend fun deletePhotoById(photoId: Long) {
        photoDao.deletePhotoById(photoId)
    }
}
