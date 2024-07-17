package com.example.fitnutrijournal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fitnutrijournal.data.database.FoodDatabase
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.data.repository.DietRepository
import kotlinx.coroutines.launch

class DietViewModel(application: Application) : AndroidViewModel(application) {

    private val _favorites = MutableLiveData<Set<String>>(emptySet())
    val favorites: LiveData<Set<String>> get() = _favorites

    // 아이템 검색
    private val _searchQuery = MutableLiveData("")
    private val searchQuery: LiveData<String> get() = _searchQuery

    // 아이템 선택
    private val _selectedFood = MutableLiveData<Food>()
    val selectedFood: LiveData<Food> get() = _selectedFood
    fun selectFood(foodCode: String) {
        viewModelScope.launch {
            val food = repository.getFoodByFoodCode(foodCode)
            _selectedFood.value = food
            updateNutrientValues(food.servingSize) // 초기값으로 기본 중량 설정
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
    private val allFoods: LiveData<List<Food>>
    val favoriteFoods: LiveData<List<Food>>
    val userAddedFoods: LiveData<List<Food>>


    private val _isCheckboxVisible = MutableLiveData<Boolean>()
    val isCheckboxVisible: LiveData<Boolean> get() = _isCheckboxVisible

    fun setCheckboxVisible(isVisible: Boolean) {
        _isCheckboxVisible.value = isVisible
    }

    private val _isSaveButtonVisible = MutableLiveData<Boolean>(false)
    val isSaveButtonVisible: LiveData<Boolean> get() = _isSaveButtonVisible

    fun setSaveButtonVisibility(isVisible: Boolean) {
        _isSaveButtonVisible.value = isVisible
    }

    init {
        val foodDao = FoodDatabase.getDatabase(application).foodDao()
        repository = DietRepository(foodDao)
        allFoods = repository.allFoods
        favoriteFoods = repository.favoriteFoods
        userAddedFoods = repository.userAddedFoods

        // Load initial favorites from the database
        favoriteFoods.observeForever { favoriteList ->
            _favorites.value = favoriteList.map { it.foodCd }.toSet()
        }

    }

    fun updateTotalContent(value: String) {
        _totalContent.value = value
        val food = _selectedFood.value ?: return
        val totalContent = value.toIntOrNull() ?: 0
        updateNutrientValues(totalContent)
    }

    private fun updateNutrientValues(totalContent: Int) {
        val food = _selectedFood.value ?: return
        if (food.servingSize == 0) return

        val factor = totalContent.toFloat() / food.servingSize
        _calculatedCalories.value = (food.calories * factor).toString()
        _calculatedCarbohydrate.value = (food.carbohydrate * factor).toString()
        _calculatedProtein.value = (food.protein * factor).toString()
        _calculatedFat.value = (food.fat * factor).toString()
    }

    val filteredFoods = MediatorLiveData<List<Food>>().apply {
        addSource(allFoods) { foods ->
            if (foods.isNotEmpty()) {
                value = filterFoods(foods, searchQuery.value.orEmpty())
            } else {
                value = emptyList()
            }
        }
        addSource(searchQuery) { query ->
            val currentFoods = allFoods.value.orEmpty()
            if (currentFoods.isNotEmpty()) {
                value = filterFoods(currentFoods, query)
            } else {
                value = emptyList()
            }
        }
    }

    private fun filterFoods(foods: List<Food>, query: String): List<Food> {
        val filtered = if (query.isEmpty()) {
            foods
        } else {
            foods.filter { it.foodName.contains(query, ignoreCase = true) }
        }
        return filtered
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(item: Food) {
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
