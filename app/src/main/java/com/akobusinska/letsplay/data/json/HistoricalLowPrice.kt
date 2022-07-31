package com.akobusinska.letsplay.data.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HistoricalLowPrice(
    @SerializedName("country")
    @Expose
    val country: String?,

    @SerializedName("date")
    @Expose
    val date: String?,

    @SerializedName("price")
    @Expose
    val price: Double?,

    @SerializedName("isLow")
    @Expose
    val isLow: Boolean?
)