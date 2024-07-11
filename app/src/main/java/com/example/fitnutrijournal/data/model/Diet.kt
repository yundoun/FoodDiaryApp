package com.example.fitnutrijournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diet_table")
data class Diet(
    @PrimaryKey val foodCode: String,
    val foodGroup: String,
    val foodName: String,
    val totalContent: Int,
    val calories: Float,
    val carbohydrate: Float,
    val protein: Float,
    val fat: Float,
    val caloriesPerGram: Float
)