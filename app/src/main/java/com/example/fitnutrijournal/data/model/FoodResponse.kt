package com.example.fitnutrijournal.data.model

import com.google.gson.annotations.SerializedName

data class FoodResponse(
    @SerializedName("I2790") val i2790: I2790
)

data class I2790(
    @SerializedName("total_count") val totalCount: String,
    @SerializedName("row") val rows: List<FoodItem>,
    @SerializedName("RESULT") val result: Result
)

data class FoodItem(
    @SerializedName("NUM") val num: String,
    @SerializedName("FOOD_CD") val foodCd: String?,
    @SerializedName("SAMPLING_REGION_NAME") val samplingRegionName: String?,
    @SerializedName("SAMPLING_MONTH_NAME") val samplingMonthName: String?,
    @SerializedName("SAMPLING_REGION_CD") val samplingRegionCd: String?,
    @SerializedName("SAMPLING_MONTH_CD") val samplingMonthCd: String?,
    @SerializedName("GROUP_NAME") val groupName: String?,
    @SerializedName("DESC_KOR") val foodName: String?,
    @SerializedName("RESEARCH_YEAR") val researchYear: String?,
    @SerializedName("MAKER_NAME") val makerName: String?,
    @SerializedName("SUB_REF_NAME") val subRefName: String?,
    @SerializedName("SERVING_SIZE") val servingSize: String?,
    @SerializedName("SERVING_UNIT") val servingUnit: String?,
    @SerializedName("NUTR_CONT1") val calories: String?,
    @SerializedName("NUTR_CONT2") val carbohydrate: String?,
    @SerializedName("NUTR_CONT3") val protein: String?,
    @SerializedName("NUTR_CONT4") val fat: String?,
    @SerializedName("NUTR_CONT5") val nutrCont5: String?,
    @SerializedName("NUTR_CONT6") val nutrCont6: String?,
    @SerializedName("NUTR_CONT7") val nutrCont7: String?,
    @SerializedName("NUTR_CONT8") val nutrCont8: String?,
    @SerializedName("NUTR_CONT9") val nutrCont9: String?
)

data class Result(
    @SerializedName("MSG") val msg: String,
    @SerializedName("CODE") val code: String
)
