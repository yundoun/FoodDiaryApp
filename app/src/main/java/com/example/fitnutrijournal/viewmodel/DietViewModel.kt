package com.example.fitnutrijournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fitnutrijournal.data.database.DietDatabase
import com.example.fitnutrijournal.data.model.Diet
import com.example.fitnutrijournal.data.repository.DietRepository
import kotlinx.coroutines.launch

class DietViewModel(application: Application) : AndroidViewModel(application) {
    private val _favorites = MutableLiveData<Set<String>>(emptySet())
    val favorites: LiveData<Set<String>> get() = _favorites

    private val repository: DietRepository
    val allDiets: LiveData<List<Diet>>
    val favoriteDiets: LiveData<List<Diet>>

    init {
        val dietDao = DietDatabase.getDatabase(application).dietDao()
        repository = DietRepository(dietDao)
        allDiets = repository.allDiets
        favoriteDiets = repository.favoriteDiets

        // Insert dummy data
        insertDummyData()
    }

    fun toggleFavorite(item: Diet) {
        viewModelScope.launch {
            val currentFavorites = _favorites.value ?: emptySet()
            if (currentFavorites.contains(item.foodCode)) {
                _favorites.value = currentFavorites - item.foodCode
                item.isFavorite = false
            } else {
                _favorites.value = currentFavorites + item.foodCode
                item.isFavorite = true
            }
            repository.update(item)
        }
    }

    private fun insertDummyData() {
        val dummyData = listOf(
            Diet("D00001", "구이류", "가자미구이", 200, 314f, 3.5f, 43.2f, 14.2f, 314f / 200),
            Diet("D00002", "구이류", "갈치구이", 250, 481.32f, 0.35f, 61.99f, 14.2f, 481.32f / 250),
            Diet("D00003", "구이류", "고등어구이", 180, 400f, 4.0f, 30.0f, 25.0f, 400f / 180),
            Diet("D00004", "구이류", "연어구이", 220, 350f, 2.0f, 40.0f, 20.0f, 350f / 220),
            Diet("D00005", "구이류", "삼치구이", 200, 300f, 5.0f, 35.0f, 10.0f, 300f / 200)
        )
        viewModelScope.launch {
            dummyData.forEach {
                repository.insert(it)
            }
        }
    }
}
