package com.example.fitnutrijournal.data.repository

import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.DailyIntakeGoalDao
import com.example.fitnutrijournal.data.model.DailyIntakeGoal

class DailyIntakeGoalRepository(private val dailyIntakeGoalDao: DailyIntakeGoalDao) {

    // 주어진 객체를 삽입하거나 업데이트
    suspend fun insertOrUpdate(goal: DailyIntakeGoal) {
        dailyIntakeGoalDao.insertOrUpdate(goal)
    }

    // 특정 날짜에 해당하는 DailyIntakeGoal 데이터를 LiveData 형태로 반환
    fun getDailyIntakeGoal(date: String): LiveData<DailyIntakeGoal> {
        return dailyIntakeGoalDao.getDailyIntakeGoal(date)
    }
}
