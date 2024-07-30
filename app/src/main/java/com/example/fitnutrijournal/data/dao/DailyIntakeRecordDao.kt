package com.example.fitnutrijournal.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitnutrijournal.data.model.DailyIntakeRecord

@Dao
interface DailyIntakeRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DailyIntakeRecord)

    @Update
    suspend fun update(record: DailyIntakeRecord)

    @Query("SELECT * FROM daily_intake_record WHERE date = :date LIMIT 1")
    suspend fun getRecordByDateSync(date: String): DailyIntakeRecord?

    @Query("SELECT * FROM daily_intake_record WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getRecordsBetweenDates(startDate: String, endDate: String): List<DailyIntakeRecord>
}
