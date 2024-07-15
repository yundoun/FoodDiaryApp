package com.example.fitnutrijournal.viewmodel

import android.app.Application
import android.util.Log
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

    // 아이템 검색
    private val _searchQuery = MutableLiveData("")
    private val searchQuery: LiveData<String> get() = _searchQuery

    // 아이템 선택
    private val _selectedDiet = MutableLiveData<Diet>()
    val selectedDiet: LiveData<Diet> get() = _selectedDiet
    fun selectDiet(foodCode: String) {
        viewModelScope.launch {
            val diet = repository.getDietByFoodCode(foodCode)
            _selectedDiet.value = diet
        }
    }

    private val repository: DietRepository
    private val allDiets: LiveData<List<Diet>>
    val favoriteDiets: LiveData<List<Diet>>

    init {
        val dietDao = DietDatabase.getDatabase(application).dietDao()
        repository = DietRepository(dietDao)
        allDiets = repository.allDiets
        favoriteDiets = repository.favoriteDiets


        // Load initial favorites from the database
        favoriteDiets.observeForever { favoriteList ->
            _favorites.value = favoriteList.map { it.foodCode }.toSet()
        }


    }


    val filteredDiets = MediatorLiveData<List<Diet>>().apply {
        addSource(allDiets) { diets ->
            //Log.d("DietViewModel", "allDiets source changed, diets: $diets")
            if (diets.isNotEmpty()) {
                value = filterDiets(diets, searchQuery.value.orEmpty())
            } else {
                value = emptyList()
            }
        }
        addSource(searchQuery) { query ->
            //Log.d("DietViewModel", "searchQuery source changed, query: $query")
            val currentDiets = allDiets.value.orEmpty()
            if (currentDiets.isNotEmpty()) {
                value = filterDiets(currentDiets, query)
            } else {
                value = emptyList()
            }
        }
    }

    private fun filterDiets(diets: List<Diet>, query: String): List<Diet> {
        val filtered = if (query.isEmpty()) {
            diets
        } else {
            diets.filter { it.foodName.contains(query, ignoreCase = true) }
        }
        //Log.d("DietViewModel", "Filtered diets with query \"$query\": $filtered")
        return filtered
    }

    fun setSearchQuery(query: String) {
       // Log.d("DietViewModel", "Setting search query: $query")
        _searchQuery.value = query
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

}
