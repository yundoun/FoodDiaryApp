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
            updateNutrientValues(diet.totalContent) // 초기값으로 기본 중량 설정
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
            _favorites.value = favoriteList.map { it.foodCode }.toSet()
        }

        // Insert dummy data
        insertDummyData()
    }

    fun updateTotalContent(value: String) {
        _totalContent.value = value
        val diet = _selectedDiet.value ?: return
        val totalContent = value.toIntOrNull() ?: 0
        updateNutrientValues(totalContent)
    }

    private fun updateNutrientValues(totalContent: Int) {
        val diet = _selectedDiet.value ?: return
        if (diet.totalContent == 0) return

        val factor = totalContent.toFloat() / diet.totalContent
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
            Diet("D00001", "구이류", "가자미구이", 200, 314f, 3.5f, 43.2f, 14.2f, 314f / 200, true,true),
            Diet("D00002", "구이류", "갈치구이", 250, 481.32f, 0.35f, 61.99f, 14.2f, 481.32f / 250),
            Diet("D00003", "구이류", "고등어구이", 180, 400f, 4.0f, 30.0f, 25.0f, 400f / 180),
            Diet("D00004", "구이류", "연어구이", 220, 350f, 2.0f, 40.0f, 20.0f, 350f / 220),
            Diet("D00005", "구이류", "삼치구이", 200, 300f, 5.0f, 35.0f, 10.0f, 300f / 200)
        )
        viewModelScope.launch {
            dummyData.forEach {
                repository.insert(it)
            }
            //Log.d("DietViewModel", "Dummy data inserted")
        }
    }

}
