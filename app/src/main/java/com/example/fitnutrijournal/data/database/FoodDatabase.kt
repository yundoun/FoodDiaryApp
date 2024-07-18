package com.example.fitnutrijournal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fitnutrijournal.data.dao.*
import com.example.fitnutrijournal.data.model.*

@Database(entities = [Food::class, Meal::class, DailyIntakeGoal::class, DailyIntakeRecord::class], version = 4, exportSchema = false)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
    abstract fun dailyIntakeGoalDao(): DailyIntakeGoalDao
    abstract fun dailyIntakeRecordDao(): DailyIntakeRecordDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        // Migration 객체 정의
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE daily_intake_record (
                        date TEXT NOT NULL,
                        currentCalories INTEGER NOT NULL,
                        currentCarbs INTEGER NOT NULL,
                        currentProtein INTEGER NOT NULL,
                        currentFat INTEGER NOT NULL,
                        PRIMARY KEY(date)
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
                    .addMigrations(MIGRATION_3_4) // 마이그레이션 추가
                    // .fallbackToDestructiveMigration() // 모든 데이터를 초기화하고 새로 시작
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
