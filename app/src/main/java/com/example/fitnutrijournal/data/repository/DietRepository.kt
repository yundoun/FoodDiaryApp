package com.example.fitnutrijournal.data.repository

import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.DietDao
import com.example.fitnutrijournal.data.model.Diet

class DietRepository(private val dietDao: DietDao) {

    val allDiets: LiveData<List<Diet>> = dietDao.getAllDiets()

    suspend fun insert(diet: Diet) {
        dietDao.insert(diet)
    }
}
