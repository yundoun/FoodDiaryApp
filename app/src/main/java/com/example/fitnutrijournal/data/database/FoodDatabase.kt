package com.example.fitnutrijournal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitnutrijournal.data.dao.FoodDao
import com.example.fitnutrijournal.data.model.Food

@Database(entities = [Food::class], version = 4, exportSchema = false)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        fun getDatabase(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "food_database"
                ).fallbackToDestructiveMigration() // 스키마 변경 시 데이터베이스를 재생성
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
