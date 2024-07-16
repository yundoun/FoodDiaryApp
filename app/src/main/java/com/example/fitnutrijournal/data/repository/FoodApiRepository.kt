package com.example.fitnutrijournal.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fitnutrijournal.data.api.RetrofitClient
import com.example.fitnutrijournal.data.model.FoodResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodApiRepository {
    private val _foodInfo = MutableLiveData<FoodResponse>()
    val foodInfo: LiveData<FoodResponse> get() = _foodInfo

    private val apiKey = "2faba1329d85403c85cc"
    private val serviceId = "I2790"
    private val dataType = "json"
    private val startIdx = 1
    private val endIdx = 5

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
                        it.i2790.rows.forEach { item ->
                            Log.d(
                                "FoodInfo",
                                "식품코드: ${item.foodCd}, 식품군: ${item.groupName}, 식품이름: ${item.descKor}, 총내용량: ${item.servingSize}, 단위: ${item.servingUnit}, 열량: ${item.nutrCont1}, 탄수화물: ${item.nutrCont2}, 단백질: ${item.nutrCont3}, 지방: ${item.nutrCont4}, 당류: ${item.nutrCont5}, 나트륨: ${item.nutrCont6}, 콜레스테롤: ${item.nutrCont7}, 포화지방산: ${item.nutrCont8}, 트랜스지방: ${item.nutrCont9}"
                            )
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
