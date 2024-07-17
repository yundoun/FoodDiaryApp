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
            updateNutrientValues(diet.servingSize) // 초기값으로 기본 중량 설정
        }
    }

    private val _totalContent = MutableLiveData<String>("")
    val totalContent: MutableLiveData<String> get() = _totalContent

    private val _calculatedCalories = MutableLiveData<String>("")
    val calculatedCalories: MutableLiveData<String> get() = _calculatedCalories

    private val _calculatedCarbohydrate = MutableLiveData<String>("")
    val calculatedCarbohydrate: MutableLiveData<String> get() = _calculatedCarbohydrate

    private val _calculatedProtein = MutableLiveData<String>("")
    val calculatedProtein: MutableLiveData<String> get() = _calculatedProtein

    private val _calculatedFat = MutableLiveData<String>("")
    val calculatedFat: MutableLiveData<String> get() = _calculatedFat

    private val repository: DietRepository
    private val allDiets: LiveData<List<Diet>>
    val favoriteDiets: LiveData<List<Diet>>
    val userAddedDiets: LiveData<List<Diet>>


    private val _isCheckboxVisible = MutableLiveData<Boolean>()
    val isCheckboxVisible: LiveData<Boolean> get() = _isCheckboxVisible

    fun setCheckboxVisible(isVisible: Boolean) {
        _isCheckboxVisible.value = isVisible
    }

    init {
        val dietDao = DietDatabase.getDatabase(application).dietDao()
        repository = DietRepository(dietDao)
        allDiets = repository.allDiets
        favoriteDiets = repository.favoriteDiets
        userAddedDiets = repository.userAddedDiets

        // Load initial favorites from the database
        favoriteDiets.observeForever { favoriteList ->
            _favorites.value = favoriteList.map { it.foodCd }.toSet()
        }

    }

    fun updateTotalContent(value: String) {
        _totalContent.value = value
        val diet = _selectedDiet.value ?: return
        val totalContent = value.toIntOrNull() ?: 0
        updateNutrientValues(totalContent)
    }

    private fun updateNutrientValues(totalContent: Int) {
        val diet = _selectedDiet.value ?: return
        if (diet.servingSize == 0) return

        val factor = totalContent.toFloat() / diet.servingSize
        _calculatedCalories.value = (diet.calories * factor).toString()
        _calculatedCarbohydrate.value = (diet.carbohydrate * factor).toString()
        _calculatedProtein.value = (diet.protein * factor).toString()
        _calculatedFat.value = (diet.fat * factor).toString()
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
            if (currentFavorites.contains(item.foodCd)) {
                _favorites.value = currentFavorites - item.foodCd
                item.isFavorite = false
            } else {
                _favorites.value = currentFavorites + item.foodCd
                item.isFavorite = true
            }
            repository.update(item)
        }
    }


}
