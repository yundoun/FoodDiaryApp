package com.example.fitnutrijournal.viewmodel

import com.example.fitnutrijournal.data.database.FoodDatabase // 여기에 import 추가
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fitnutrijournal.data.model.DailyIntakeRecord
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.data.model.Meal
import com.example.fitnutrijournal.data.model.MealWithFood
import com.example.fitnutrijournal.data.repository.DailyIntakeRecordRepository
import com.example.fitnutrijournal.data.repository.FoodRepository
import com.example.fitnutrijournal.data.repository.MealRepository
import kotlinx.coroutines.launch

class DietViewModel(application: Application, private val homeViewModel: HomeViewModel) : AndroidViewModel(application) {

    private val mealRepository: MealRepository
    private val foodRepository: FoodRepository
    private val dailyIntakeRecordRepository: DailyIntakeRecordRepository

    private val _dailyIntakeRecord = MutableLiveData<DailyIntakeRecord?>()
    val dailyIntakeRecord: LiveData<DailyIntakeRecord?> get() = _dailyIntakeRecord

    init {
        val database = FoodDatabase.getDatabase(application)
        val dailyIntakeRecordDao = database.dailyIntakeRecordDao()
        val foodDao = database.foodDao()
        val mealDao = database.mealDao()

        dailyIntakeRecordRepository = DailyIntakeRecordRepository(dailyIntakeRecordDao)
        foodRepository = FoodRepository(foodDao)
        mealRepository = MealRepository(mealDao)
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
            val food = foodRepository.getFoodByFoodCode(foodCode)
            _selectedFood.value = food

            // 기본 중량으로 설정하지 않고 _selectedMealQuantity 값이 있을 때만 업데이트
            if (_selectedMealQuantity.value == null) {
                updateNutrientValues(food.servingSize)
            }
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


    private val allFoods: LiveData<List<Food>> = foodRepository.allFoods
    val favoriteFoods: LiveData<List<Food>> = foodRepository.favoriteFoods
    val userAddedFoods: LiveData<List<Food>> = foodRepository.userAddedFoods

    private val _isCheckboxVisible = MutableLiveData<Boolean?>()
    val isCheckboxVisible: LiveData<Boolean?> get() = _isCheckboxVisible

    fun setCheckboxVisible(isVisible: Boolean?) {
        _isCheckboxVisible.value = isVisible
    }

    private val _isSaveButtonVisible = MutableLiveData<Boolean>(false)
    val isSaveButtonVisible: LiveData<Boolean> get() = _isSaveButtonVisible
    private val _isUpdateButtonVisible = MutableLiveData<Boolean>(false)
    val isUpdateButtonVisible: LiveData<Boolean> get() = _isUpdateButtonVisible

    private val _checkedItems = MutableLiveData<Set<Food>>(emptySet())
    val checkedItems: LiveData<Set<Food>> get() = _checkedItems

    // 식사 타입에 따라 데이터 필터링
    private val _mealType = MutableLiveData<String>("")
    val mealType: LiveData<String> get() = _mealType

    private val _quantity = MutableLiveData<Float>()
    val quantity: LiveData<Float> get() = _quantity

    private val _mealsWithFood = MutableLiveData<List<MealWithFood>>()
    val mealsWithFood: LiveData<List<MealWithFood>> get() = _mealsWithFood

    private val _selectedMealQuantity = MutableLiveData<Int?>()
    val selectedMealQuantity: LiveData<Int?> get() = _selectedMealQuantity

    private suspend fun generateFoodCode(): String {
        val maxFoodCd = foodRepository.getMaxFoodCd()
        return if (maxFoodCd != null) {
            val numberPart = maxFoodCd.substring(1).toInt()
            val newNumberPart = numberPart + 1
            "D" + newNumberPart.toString().padStart(5, '0')
        } else {
            "D00001"
        }
    }

    fun insertFood(food: Food) {
        viewModelScope.launch {
            val newFoodCd = generateFoodCode()
            val newFood = food.copy(foodCd = newFoodCd)
            foodRepository.insert(newFood)
        }
    }


    fun setMealType(type: String) {
        _mealType.value = type
    }

    private val _selectedCountFoodItem = MutableLiveData<Int>(0)
    val selectedCountFoodItem: LiveData<Int> get() = _selectedCountFoodItem

    fun clearSelectedCountFoodItem() {
        _selectedCountFoodItem.value = 0
    }

    fun setSaveButtonVisibility(isVisible: Boolean) {
        _isSaveButtonVisible.value = isVisible
    }

    fun setUpdateButtonVisibility(isVisible: Boolean) {
        _isUpdateButtonVisible.value = isVisible
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
        val totalContent = value.toIntOrNull() ?: 0
        updateNutrientValues(totalContent)
    }

    // 중량에 맞게 영양성분 계산
    private fun updateNutrientValues(totalContent: Int) {
        val food = _selectedFood.value ?: return
        if (food.servingSize == 0) return

        Log.d("DietViewModel", "Calculating nutrients with totalContent: $totalContent and servingSize: ${food.servingSize}")

        val factor = totalContent.toFloat() / food.servingSize
        _calculatedCalories.value = String.format("%.2f", food.calories * factor)
        _calculatedCarbohydrate.value = String.format("%.2f", food.carbohydrate * factor)
        _calculatedProtein.value = String.format("%.2f", food.protein * factor)
        _calculatedFat.value = String.format("%.2f", food.fat * factor)

        Log.d("DietViewModel", "Calculated calories: ${_calculatedCalories.value}, carbs: ${_calculatedCarbohydrate.value}, protein: ${_calculatedProtein.value}, fat: ${_calculatedFat.value}")
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
            foodRepository.update(item)
        }
    }

    // 사용자가 설정 중량 저장
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveCurrentFoodIntake() {
        viewModelScope.launch {
            val food = _selectedFood.value ?: return@launch
            val date = homeViewModel.currentDate.value ?: return@launch
            val mealType = _mealType.value ?: return@launch
            val totalContent = _totalContent.value?.toIntOrNull() ?: return@launch

            val meal = Meal(
                date = date,
                mealType = mealType,
                dietFoodCode = food.foodCd,
                quantity = totalContent
            )

            val calories = _calculatedCalories.value?.toFloatOrNull() ?: 0f
            val carbs = _calculatedCarbohydrate.value?.toFloatOrNull() ?: 0f
            val protein = _calculatedProtein.value?.toFloatOrNull() ?: 0f
            val fat = _calculatedFat.value?.toFloatOrNull() ?: 0f

            val initialRecord = dailyIntakeRecordRepository.getRecordByDate(date)
                ?: DailyIntakeRecord(date)

            val updatedRecord = initialRecord.copy(
                currentCalories = (initialRecord.currentCalories + calories).toInt(),
                currentCarbs = (initialRecord.currentCarbs + carbs).toInt(),
                currentProtein = (initialRecord.currentProtein + protein).toInt(),
                currentFat = (initialRecord.currentFat + fat).toInt()
            )

            mealRepository.insert(meal)
            dailyIntakeRecordRepository.insert(updatedRecord)
            _dailyIntakeRecord.postValue(updatedRecord)

            Log.d(
                "DietViewModel",
                "Saved meal: ${meal.dietFoodCode}, Date: ${meal.date}, Meal type: ${meal.mealType}, Quantity: ${meal.quantity}"
            )

            Log.d(
                "DietViewModel",
                "Updated record: ${updatedRecord.date}, Calories: ${updatedRecord.currentCalories}, Carbs: ${updatedRecord.currentCarbs}, Protein: ${updatedRecord.currentProtein}, Fat: ${updatedRecord.currentFat}"
            )


            homeViewModel.updateNutrientData(mealType, food, totalContent)
        }
    }


    // Food 데이터 삭제
    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteFood(food: Food) {
        viewModelScope.launch {
            // 식사에 해당 음식이 포함되어 있는지 확인
            val mealsWithFood = mealRepository.getMealsByFoodCode(food.foodCd)
            mealsWithFood.forEach { meal ->
                val quantity = meal.quantity
                val mealType = meal.mealType
                Log.d("DietViewModel", "Deleting meal: $meal with food: $food")
                // 섭취량 감소
                homeViewModel.updateNutrientData(mealType, food, -quantity)
                // 식사 데이터 삭제
                mealRepository.deleteMealById(meal.id)
            }
            Log.d("DietViewModel", "Deleting food: $food")

            // 음식 데이터 삭제
            foodRepository.delete(food)
            // 섭취 기록 업데이트
            homeViewModel.refreshFilteredFoods()
        }
    }

    fun setSelectedMealQuantity(quantity: Int) {
        _selectedMealQuantity.value = quantity
    }

    fun clearSelectedMealQuantity() {
        _selectedMealQuantity.value = null
    }

    fun loadMealsWithFood() {
        viewModelScope.launch {
            val mealWithFoodList = mealRepository.getAllMealsWithFood()
            _mealsWithFood.postValue(mealWithFoodList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteMealById(mealId: Long) {
        viewModelScope.launch {
            val mealWithFood = mealRepository.getMealWithFoodById(mealId)
            if (mealWithFood != null) {
                val quantity = mealWithFood.meal.quantity
                val mealType = mealWithFood.meal.mealType
                Log.d("DietViewModel", "Deleting meal: ${mealWithFood.meal}")
                // 섭취량 감소
                homeViewModel.updateNutrientData(mealType, mealWithFood.food, -quantity)
                // 식사 데이터 삭제
                mealRepository.deleteMealById(mealId)
                Log.d("DietViewModel", "Deleted meal with id: $mealId")

                // 섭취 기록 업데이트
                homeViewModel.refreshFilteredFoods()
            } else {
                Log.d("DietViewModel", "Meal not found with id: $mealId")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateFoodIntake() {
        val food = _selectedFood.value ?: return
        val newQuantity = _totalContent.value?.toIntOrNull() ?: return
        viewModelScope.launch {
            val date = homeViewModel.currentDate.value ?: return@launch
            val mealType = _mealType.value ?: return@launch

            val meal = mealRepository.getMealByFoodCodeAndDate(food.foodCd, date)
            meal?.let {
                val oldQuantity = it.quantity

                val updatedMeal = it.copy(quantity = newQuantity)
                mealRepository.update(updatedMeal)

                // 기존 섭취량을 제거하고, 새로운 섭취량을 추가하여 영양성분 업데이트
                homeViewModel.updateNutrientData(mealType, food, -oldQuantity)
                homeViewModel.updateNutrientData(mealType, food, newQuantity)

                homeViewModel.refreshFilteredFoods()
            }
        }
    }

}
