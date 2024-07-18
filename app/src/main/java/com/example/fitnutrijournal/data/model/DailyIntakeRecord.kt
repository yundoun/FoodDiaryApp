package com.example.fitnutrijournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_intake_record")
data class DailyIntakeRecord(
    @PrimaryKey val date: String,
    var currentCalories: Int = 0,
    var currentCarbs: Int = 0,
    var currentProtein: Int = 0,
    var currentFat: Int = 0
)


//data class NutrientData(
//    var calories: Int = 0,
//    var carbs: Float = 0f,
//    var protein: Float = 0f,
//    var fat: Float = 0f,
//    var quantity: Float = 0f
//)
