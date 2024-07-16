package com.example.fitnutrijournal.data.api

import com.example.fitnutrijournal.data.model.FoodResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApiService {
    @GET("api/{keyId}/{serviceId}/{dataType}/{startIdx}/{endIdx}")
    fun getFoodInfo(
        @Path("keyId") keyId: String,
        @Path("serviceId") serviceId: String,
        @Path("dataType") dataType: String,
        @Path("startIdx") startIdx: Int,
        @Path("endIdx") endIdx: Int,
        @Query("DESC_KOR") descKor: String? = null
    ): Call<FoodResponse>
}
