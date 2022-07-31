package com.akobusinska.letsplay.data.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Images(
    @SerializedName("thumb")
    @Expose
    val thumb: String?,
    @SerializedName("small")
    @Expose
    val small: String?,
    @SerializedName("medium")
    @Expose
    val medium: String?,
    @SerializedName("large")
    @Expose
    val large: String?,
    @SerializedName("original")
    @Expose
    val original: String?
)