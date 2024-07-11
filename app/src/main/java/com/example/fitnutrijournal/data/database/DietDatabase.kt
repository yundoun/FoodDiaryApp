package com.example.fitnutrijournal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitnutrijournal.data.dao.DietDao
import com.example.fitnutrijournal.data.model.Diet

@Database(entities = [Diet::class], version = 2, exportSchema = false) // 버전 번호를 2로 증가시킴
abstract class DietDatabase : RoomDatabase() {
    abstract fun dietDao(): DietDao

    companion object {
        @Volatile
        private var INSTANCE: DietDatabase? = null

        fun getDatabase(context: Context): DietDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DietDatabase::class.java,
                    "diet_database"
                ).fallbackToDestructiveMigration() // 스키마 변경 시 데이터베이스를 재생성
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
