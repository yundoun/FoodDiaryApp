package com.example.fitnutrijournal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fitnutrijournal.data.dao.DailyIntakeGoalDao
import com.example.fitnutrijournal.data.dao.FoodDao
import com.example.fitnutrijournal.data.dao.MealDao
import com.example.fitnutrijournal.data.model.DailyIntakeGoal
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.data.model.Meal

@Database(entities = [Food::class, Meal::class, DailyIntakeGoal::class], version = 3, exportSchema = false)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun mealDao(): MealDao
    abstract fun dailyIntakeGoalDao(): DailyIntakeGoalDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        // Migration 객체 정의
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE daily_intake_goal (
                        date TEXT NOT NULL, 
                        targetCalories INTEGER NOT NULL, 
                        targetBreakfast INTEGER NOT NULL, 
                        targetLunch INTEGER NOT NULL, 
                        targetDinner INTEGER NOT NULL, 
                        targetSnack INTEGER NOT NULL,
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
                    .addMigrations(MIGRATION_2_3) // 마이그레이션 추가
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
