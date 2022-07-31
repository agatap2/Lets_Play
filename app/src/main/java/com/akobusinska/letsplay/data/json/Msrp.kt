package com.akobusinska.letsplay.data.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Msrp(
    @SerializedName("country")
    @Expose
    val country: String?,

    @SerializedName("price")
    @Expose
    val price: Double?
)