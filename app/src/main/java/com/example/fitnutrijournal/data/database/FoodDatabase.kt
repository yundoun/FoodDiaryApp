package com.example.fitnutrijournal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fitnutrijournal.data.dao.*
import com.example.fitnutrijournal.data.model.*

@Database(entities = [Food::class, Meal::class, DailyIntakeGoal::class, DailyIntakeRecord::class], version = 10, exportSchema = false)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
    abstract fun dailyIntakeGoalDao(): DailyIntakeGoalDao
    abstract fun dailyIntakeRecordDao(): DailyIntakeRecordDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        // Migration 객체 정의
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // food_table에서 Food 테이블로 데이터 복사
                db.execSQL("""
                    INSERT INTO Food (foodCd, foodName, servingSize, calories, carbohydrate, protein, fat, caloriesPerGram, isFavorite, isAddedByUser)
                    SELECT foodCd, foodName, servingSize, calories, carbohydrate, protein, fat, caloriesPerGram, isFavorite, isAddedByUser FROM food_table
                """)

                // food_table 삭제
                db.execSQL("DROP TABLE IF EXISTS food_table")
            }
        }

        fun getDatabase(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "food_database"
                )
                    .addMigrations(MIGRATION_5_6, MIGRATION_5_6) // 마이그레이션 추가
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
