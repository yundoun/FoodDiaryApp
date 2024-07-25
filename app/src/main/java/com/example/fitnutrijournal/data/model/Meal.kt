// Meal.kt

package com.example.fitnutrijournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val mealType: String, // "아침", "점심", "저녁", "간식"
    val dietFoodCode: String,
    val quantity: Int // 섭취한 양(g)
)
