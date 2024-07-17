package com.example.fitnutrijournal.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

// Room 데이터베이스의 Entity로 사용 -> 데이터 정의
@Entity(tableName = "food_table")
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
