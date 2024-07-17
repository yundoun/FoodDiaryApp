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
