// Food.kt

package com.example.fitnutrijournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Food(
    @PrimaryKey val foodCd: String,
    val foodName: String,
    val servingSize: Int,
    val calories: Float,
    val carbohydrate: Float,
    val protein: Float,
    val fat: Float,
    val caloriesPerGram: Float,
    var isFavorite: Boolean = false,
    var isAddedByUser: Boolean = false
)
