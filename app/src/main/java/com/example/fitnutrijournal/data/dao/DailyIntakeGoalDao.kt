package com.example.fitnutrijournal.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnutrijournal.data.model.DailyIntakeGoal

@Dao
interface DailyIntakeGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(dailyIntakeGoal: DailyIntakeGoal)

    @Query("SELECT * FROM daily_intake_goal WHERE date = :date")
    fun getDailyIntakeGoal(date: String): LiveData<DailyIntakeGoal>
}
