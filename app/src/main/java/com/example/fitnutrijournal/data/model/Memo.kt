package com.example.fitnutrijournal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "memos")
data class Memo(
    @PrimaryKey val date: String,
    val content: String
)