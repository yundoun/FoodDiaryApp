package com.example.fitnutrijournal.data.repository

import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.DietDao
import com.example.fitnutrijournal.data.model.Diet

class DietRepository(private val dietDao: DietDao) {
    val allDiets: LiveData<List<Diet>> = dietDao.getAllDiets()
    val favoriteDiets: LiveData<List<Diet>> = dietDao.getFavoriteDiets()

    suspend fun insert(diet: Diet) {
        dietDao.insert(diet)
    }

    suspend fun update(diet: Diet) {
        dietDao.update(diet)
    }
}
