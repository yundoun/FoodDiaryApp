package com.example.fitnutrijournal.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fitnutrijournal.data.api.RetrofitClient
import com.example.fitnutrijournal.data.model.FoodResponse
import com.example.fitnutrijournal.data.model.Food
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoodApiRepository(private val foodRepository: FoodRepository) {
    private val _foodInfo = MutableLiveData<FoodResponse>()

    private val apiKey = "2faba1329d85403c85cc"
    private val serviceId = "I2790"
    private val dataType = "json"
    private val startIdx = 10000
    private val endIdx = 11000

    fun fetchFoodInfo(query: String? = null): LiveData<FoodResponse> {
        val call = RetrofitClient.foodApiService.getFoodInfo(
            apiKey,
            serviceId,
            dataType,
            startIdx,
            endIdx,
            query
        )
        call.enqueue(object : Callback<FoodResponse> {
            override fun onResponse(call: Call<FoodResponse>, response: Response<FoodResponse>) {
                if (response.isSuccessful) {
                    Log.d("FoodInfo", "응답 성공: ${response.body()}")
                    response.body()?.let {
                        _foodInfo.postValue(it)
                        CoroutineScope(Dispatchers.IO).launch {
                            val foods = it.i2790.rows?.mapNotNull { item ->
                                val foodCd = item.foodCd ?: return@mapNotNull null
                                val foodName = item.foodName ?: return@mapNotNull null
                                val servingSize = item.servingSize?.toIntOrNull() ?: return@mapNotNull null
                                val calories = item.calories?.toFloatOrNull() ?: return@mapNotNull null
                                val carbohydrate = item.carbohydrate?.toFloatOrNull() ?: return@mapNotNull null
                                val protein = item.protein?.toFloatOrNull() ?: return@mapNotNull null
                                val fat = item.fat?.toFloatOrNull() ?: return@mapNotNull null

                                Food(
                                    foodCd = foodCd,
                                    foodName = foodName,
                                    servingSize = servingSize,
                                    calories = calories,
                                    carbohydrate = carbohydrate,
                                    protein = protein,
                                    fat = fat,
                                    caloriesPerGram = calories / servingSize,
                                    isFavorite = false,
                                    isAddedByUser = false
                                )
                            } ?: listOf() // Add null check for `rows` and provide a default empty list

                            foodRepository.mergeAndInsertAll(foods)
                        }
                    }
                } else {
                    Log.e("FoodInfo", "응답 실패: ${response.code()}")
                    Log.e("FoodInfo", "응답 메시지: ${response.message()}")
                    response.errorBody()?.let { Log.e("FoodInfo", "에러 바디: ${it.string()}") }
                }
            }

            override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                Log.e("FoodInfo", "네트워크 에러: ${t.message}")
            }
        })
        return _foodInfo
    }
}
