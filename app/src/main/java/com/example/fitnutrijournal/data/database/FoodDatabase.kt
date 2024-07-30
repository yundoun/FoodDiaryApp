package com.example.fitnutrijournal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fitnutrijournal.data.dao.*
import com.example.fitnutrijournal.data.model.*

@Database(entities = [Food::class, Meal::class, DailyIntakeGoal::class, DailyIntakeRecord::class, Memo::class], version = 11, exportSchema = false)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
    abstract fun dailyIntakeGoalDao(): DailyIntakeGoalDao
    abstract fun dailyIntakeRecordDao(): DailyIntakeRecordDao
    abstract fun memoDao(): MemoDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        // Migration 객체 정의
        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Memo 테이블 생성
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS memos (
                        date TEXT PRIMARY KEY NOT NULL,
                        content TEXT NOT NULL
                    )
                """)
            }
        }

        fun getDatabase(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "food_database"
                )
                    .addMigrations(MIGRATION_10_11) // 마이그레이션 추가
                    //.fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
