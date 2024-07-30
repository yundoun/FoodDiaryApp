package com.example.fitnutrijournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.fitnutrijournal.data.database.FoodDatabase
import com.example.fitnutrijournal.data.model.Photo
import com.example.fitnutrijournal.data.repository.PhotoRepository
import kotlinx.coroutines.launch

class PhotoViewModel (application: Application) : AndroidViewModel(application) {


    private val photoRepository: PhotoRepository

    init {
        val database = FoodDatabase.getDatabase(application)
        val photoDao = database.photoDao()
        photoRepository = PhotoRepository(photoDao)
    }

    fun addPhoto(date: String, mealType: String, photoUri: String) {
        viewModelScope.launch {
            val photo = Photo(date = date, mealType = mealType, photoUri = photoUri)
            photoRepository.insert(photo)
        }
    }

    fun getPhotoByDateAndMealType(date: String, mealType: String): LiveData<Photo?> {
        return photoRepository.getPhotoByDateAndMealType(date, mealType)
    }

    fun deletePhotoById(photoId: Long) {
        viewModelScope.launch {
            photoRepository.deletePhotoById(photoId)
        }
    }





}