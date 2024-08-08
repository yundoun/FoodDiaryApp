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
){
    val initial: Char
        get() = foodName.firstOrNull()?.let { getInitial(it) } ?: '#'

    companion object {
        private val initialMap = mapOf(
            'ㄱ' to '가', 'ㄴ' to '나', 'ㄷ' to '다', 'ㄹ' to '라', 'ㅁ' to '마',
            'ㅂ' to '바', 'ㅅ' to '사', 'ㅇ' to '아', 'ㅈ' to '자', 'ㅊ' to '차',
            'ㅋ' to '카', 'ㅌ' to '타', 'ㅍ' to '파', 'ㅎ' to '하'
        )

        private fun getInitial(char: Char): Char {
            return when (char) {
                in '가'..'깋' -> 'ㄱ'
                in '나'..'닣' -> 'ㄴ'
                in '다'..'딯' -> 'ㄷ'
                in '라'..'맇' -> 'ㄹ'
                in '마'..'밓' -> 'ㅁ'
                in '바'..'빟' -> 'ㅂ'
                in '사'..'싷' -> 'ㅅ'
                in '아'..'잏' -> 'ㅇ'
                in '자'..'짛' -> 'ㅈ'
                in '차'..'칳' -> 'ㅊ'
                in '카'..'킿' -> 'ㅋ'
                in '타'..'팋' -> 'ㅌ'
                in '파'..'핗' -> 'ㅍ'
                in '하'..'힣' -> 'ㅎ'
                else -> '#'
            }
        }
    }
}
