package com.example.fitnutrijournal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitnutrijournal.data.dao.DietDao
import com.example.fitnutrijournal.data.model.Diet

@Database(entities = [Diet::class], version = 1, exportSchema = false)
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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
