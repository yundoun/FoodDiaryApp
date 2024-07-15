package com.example.fitnutrijournal.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

// Room 데이터베이스의 Entity로 사용 -> 데이터 정의
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
    val caloriesPerGram: Float,
    var isFavorite: Boolean = false,
    var isAddedByUser: Boolean = false // 새 필드 추가
)