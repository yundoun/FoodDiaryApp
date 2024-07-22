package com.example.fitnutrijournal.viewmodel

import com.example.fitnutrijournal.data.database.FoodDatabase // 여기에 import 추가
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fitnutrijournal.data.model.DailyIntakeRecord
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.data.model.Meal
import com.example.fitnutrijournal.data.repository.DailyIntakeRecordRepository
import com.example.fitnutrijournal.data.repository.DietRepository
import com.example.fitnutrijournal.data.repository.MealRepository
import kotlinx.coroutines.launch

class DietViewModel(application: Application) : AndroidViewModel(application) {

    private val mealRepository: MealRepository
    private val dietRepository: DietRepository
    private val dailyIntakeRecordRepository: DailyIntakeRecordRepository

    private val _dailyIntakeRecord = MutableLiveData<DailyIntakeRecord?>()
    val dailyIntakeRecord: LiveData<DailyIntakeRecord?> get() = _dailyIntakeRecord


    init {
        val database = FoodDatabase.getDatabase(application)
        val foodDao = database.foodDao()
        val mealDao = database.mealDao()
        val dailyIntakeRecordDao = database.dailyIntakeRecordDao()

        dietRepository = DietRepository(foodDao)
        mealRepository = MealRepository(mealDao)
        dailyIntakeRecordRepository = DailyIntakeRecordRepository(dailyIntakeRecordDao)
    }

    // 식단 기록 관련 메서드
    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.insert(meal)
        }
    }

    fun getMealsByDate(date: String): LiveData<List<Meal>> {
        return mealRepository.getMealsByDate(date)
    }

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
            val food = dietRepository.getFoodByFoodCode(foodCode)
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

    val allFoods: LiveData<List<Food>> = dietRepository.allFoods
    val favoriteFoods: LiveData<List<Food>> = dietRepository.favoriteFoods
    val userAddedFoods: LiveData<List<Food>> = dietRepository.userAddedFoods

    private val _isCheckboxVisible = MutableLiveData<Boolean?>()
    val isCheckboxVisible: LiveData<Boolean?> get() = _isCheckboxVisible

    fun setCheckboxVisible(isVisible: Boolean?) {
        _isCheckboxVisible.value = isVisible
    }

    private val _isSaveButtonVisible = MutableLiveData<Boolean>(false)
    val isSaveButtonVisible: LiveData<Boolean> get() = _isSaveButtonVisible

    private val _checkedItems = MutableLiveData<Set<Food>>(emptySet())
    val checkedItems: LiveData<Set<Food>> get() = _checkedItems

    //
    private val _mealType = MutableLiveData<String>("")
    val mealType: LiveData<String> get() = _mealType

    private val _quantity = MutableLiveData<Float>()
    val quantity: LiveData<Float> get() = _quantity

    private val _currentDate = MutableLiveData<String>("")
    val currentDate: LiveData<String> get() = _currentDate

    fun setMealType(type: String) {
        _mealType.value = type
    }

    fun setCurrentDate(date: String) {
        _currentDate.value = date
    }


    private val _selectedCountFoodItem = MutableLiveData<Int>(0)
    val selectedCountFoodItem: LiveData<Int> get() = _selectedCountFoodItem

    fun setSaveButtonVisibility(isVisible: Boolean) {
        _isSaveButtonVisible.value = isVisible
    }

    init {
        // Load initial favorites from the database
        favoriteFoods.observeForever { favoriteList ->
            _favorites.value = favoriteList.map { it.foodCd }.toSet()
        }
    }


    // 체크 상태 업데이트 메서드
    fun toggleCheckedItem(item: Food) {
        val currentCheckedItems = _checkedItems.value ?: emptySet()
        if (currentCheckedItems.contains(item)) {
            _checkedItems.value = currentCheckedItems - item
        } else {
            _checkedItems.value = currentCheckedItems + item
        }
        _checkedItems.value = _checkedItems.value // 트리거
        logCheckedItems()
    }

    // 체크된 아이템 초기화 메서드
    fun clearCheckedItems() {
        _checkedItems.value = emptySet()
        _checkedItems.value = _checkedItems.value // 트리거
    }


    // 체크된 아이템 로그 출력 메서드
    private fun logCheckedItems() {
        val checkedItems = _checkedItems.value ?: return
        _selectedCountFoodItem.value = checkedItems.size
        Log.d("DietViewModel", "Checked items count: ${checkedItems.size}")
        checkedItems.forEach { item ->
            Log.d(
                "DietViewModel",
                "Checked item: ${item.foodCd}, serving size: ${item.servingSize}, "
            )
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
            dietRepository.update(item)
        }
    }


}
