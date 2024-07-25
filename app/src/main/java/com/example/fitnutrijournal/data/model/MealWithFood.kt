package com.example.fitnutrijournal.data.model

import androidx.room.Embedded

data class MealWithFood(
    @Embedded val meal: Meal,
    @Embedded val food: Food
)
