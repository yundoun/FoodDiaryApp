package com.example.fitnutrijournal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DietViewModel : ViewModel() {
    private val _favorites = MutableLiveData<Set<String>>(emptySet())
    val favorites: LiveData<Set<String>>
        get() = _favorites

    fun toggleFavorite(item: String) {
        val currentFavorites = _favorites.value ?: emptySet()
        _favorites.value = if (currentFavorites.contains(item)) {
            currentFavorites - item
        } else {
            currentFavorites + item
        }
    }
}
