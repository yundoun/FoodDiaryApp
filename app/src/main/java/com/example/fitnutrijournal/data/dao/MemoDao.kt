package com.example.fitnutrijournal.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.fitnutrijournal.data.model.Memo
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {

    @Query("SELECT * FROM memos WHERE date = :date")
    suspend fun getMemoByDate(date: String): Memo?

    @Query("SELECT * FROM memos WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getMemosBetweenDates(startDate: String, endDate: String): List<Memo>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(memo: Memo): Long

    @Update
    suspend fun update(memo: Memo)

    @Transaction
    suspend fun insertOrUpdate(memo: Memo) {
        val id = insert(memo)
        if (id == -1L) {
            update(memo)
        }
    }

    @Query("DELETE FROM memos WHERE date = :date")
    suspend fun deleteByDate(date: String)
}

