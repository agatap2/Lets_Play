package com.akobusinska.letsplay.data.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PrimaryDesigner(
    @SerializedName("id")
    @Expose
    val id: String?,

    @SerializedName("name")
    @Expose
    val name: String?,

    @SerializedName("url")
    @Expose
    val url: String?
)