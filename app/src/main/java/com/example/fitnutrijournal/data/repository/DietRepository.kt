package com.example.fitnutrijournal.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.data.dao.DietDao
import com.example.fitnutrijournal.data.model.Diet

// 데이터 소스를 관리하고, 데이터베이스와 ViewModel간의 통신 담당
class DietRepository(private val dietDao: DietDao) {
    val allDiets: LiveData<List<Diet>> = dietDao.getAllDiets()
    val favoriteDiets: LiveData<List<Diet>> = dietDao.getFavoriteDiets()
    val userAddedDiets: LiveData<List<Diet>> = dietDao.getUserAddedDiets()

    suspend fun insert(diet: Diet) {
        dietDao.insert(diet)
        Log.d("DietRepository", "Diet inserted: $diet")
    }

    suspend fun update(diet: Diet) {
        dietDao.update(diet)
        Log.d("DietRepository", "Diet updated: $diet")
    }

    suspend fun getDietByFoodCode(foodCode: String): Diet {
        return dietDao.getDietByFoodCode(foodCode)
    }
}
