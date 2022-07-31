package com.akobusinska.letsplay.data.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SkuObject(
    @SerializedName("name")
    @Expose
    val name: String?,
    @SerializedName("sku")
    @Expose
    val sku: String?
)