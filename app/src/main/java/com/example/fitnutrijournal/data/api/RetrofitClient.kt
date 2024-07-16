package com.example.fitnutrijournal.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://openapi.foodsafetykorea.go.kr/"

    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val foodApiService: FoodApiService by lazy {
        instance.create(FoodApiService::class.java)
    }
}
