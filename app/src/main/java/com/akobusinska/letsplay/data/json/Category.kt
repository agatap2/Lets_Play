package com.akobusinska.letsplay.data.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")
    @Expose
    val id: String?,

    @SerializedName("url")
    @Expose
    val url: String?
)