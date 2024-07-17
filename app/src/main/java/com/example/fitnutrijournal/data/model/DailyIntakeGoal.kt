package com.example.fitnutrijournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_intake_goal")
data class DailyIntakeGoal(
    @PrimaryKey val date: String,
    val targetCalories: Int,
    val targetBreakfast: Int,
    val targetLunch: Int,
    val targetDinner: Int,
    val targetSnack: Int,
)
