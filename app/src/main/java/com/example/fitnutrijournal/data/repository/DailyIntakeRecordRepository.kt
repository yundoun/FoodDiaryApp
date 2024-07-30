package com.example.fitnutrijournal.data.repository

import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.DailyIntakeRecordDao
import com.example.fitnutrijournal.data.model.DailyIntakeRecord

class DailyIntakeRecordRepository(private val dailyIntakeRecordDao: DailyIntakeRecordDao) {

    // 주어진 DailyIntakeRecord 객체를 삽입하는 역할
    suspend fun insert(dailyIntakeRecord: DailyIntakeRecord) {
        dailyIntakeRecordDao.insert(dailyIntakeRecord)
    }

    // 주어진 DailyIntakeRecord 객체를 업데이트하는 역할
    suspend fun update(record: DailyIntakeRecord) {
        dailyIntakeRecordDao.update(record)
    }

    // 특정 날짜(date)에 해당하는 DailyIntakeRecord 데이터를 반환
    suspend fun getRecordByDate(date: String): DailyIntakeRecord? {
        return dailyIntakeRecordDao.getRecordByDateSync(date)
    }

    // 특정 기간(startDate ~ endDate) 내의 DailyIntakeRecord 데이터를 반환
    suspend fun getRecordsBetweenDates(startDate: String, endDate: String): List<DailyIntakeRecord> {
        return dailyIntakeRecordDao.getRecordsBetweenDates(startDate, endDate)
    }
}
