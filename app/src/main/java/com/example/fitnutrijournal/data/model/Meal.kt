// Meal.kt

package com.example.fitnutrijournal.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["date", "mealType", "dietFoodCode"])
data class Meal(
    val date: String,
    val mealType: String, // "아침", "점심", "저녁", "간식"
    val dietFoodCode: String,
    val quantity: Float // 섭취한 양(g)
)
