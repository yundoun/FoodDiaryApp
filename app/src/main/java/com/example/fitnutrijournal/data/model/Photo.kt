// Photo.kt
package com.example.fitnutrijournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val mealType: String,
    val photoUri: String
)
