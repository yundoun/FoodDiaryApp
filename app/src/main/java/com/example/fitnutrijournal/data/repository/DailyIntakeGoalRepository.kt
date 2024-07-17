package com.example.fitnutrijournal.data.repository

import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.DailyIntakeGoalDao
import com.example.fitnutrijournal.data.model.DailyIntakeGoal

class DailyIntakeGoalRepository(private val dailyIntakeGoalDao: DailyIntakeGoalDao) {

    suspend fun insertOrUpdate(goal: DailyIntakeGoal) {
        dailyIntakeGoalDao.insertOrUpdate(goal)
    }

    fun getDailyIntakeGoal(date: String): LiveData<DailyIntakeGoal> {
        return dailyIntakeGoalDao.getDailyIntakeGoal(date)
    }
}
