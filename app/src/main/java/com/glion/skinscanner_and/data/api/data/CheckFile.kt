package com.glion.skinscanner_and.data.api.data

import com.google.gson.annotations.SerializedName

data class RequestCheckFile(
    @SerializedName("value") val fileHash: String
)

data class ResponseCheckFile(
    val needUpdate: Boolean
)