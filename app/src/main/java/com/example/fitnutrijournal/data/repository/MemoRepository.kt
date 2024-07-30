package com.example.fitnutrijournal.data.repository

import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.MemoDao
import com.example.fitnutrijournal.data.model.Memo
import kotlinx.coroutines.flow.Flow

class MemoRepository(private val memoDao: MemoDao) {

    suspend fun getMemoByDate(date: String): Memo? {
        return memoDao.getMemoByDate(date)
    }

    suspend fun getMemosBetweenDates(startDate: String, endDate: String): List<Memo> {
        return memoDao.getMemosBetweenDates(startDate, endDate)
    }

    suspend fun insertOrUpdate(memo: Memo) {
        memoDao.insertOrUpdate(memo)
    }

    suspend fun insert(memo: Memo) {
        memoDao.insert(memo)
    }

    suspend fun update(memo: Memo) {
        memoDao.update(memo)
    }

    suspend fun deleteByDate(date: String) {
        memoDao.deleteByDate(date)
    }
}

