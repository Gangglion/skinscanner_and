package com.glion.skinscanner_and.data.api.data

import com.google.gson.annotations.SerializedName

/**
 * 키 교환 API Request
 * @property [encryptRsaKey] RSA 공개키
 */
data class RequestExchangeKey(
    @SerializedName("pKey") val encryptRsaKey: String // PEM 방식
)

/**
 * 키 교환 API Response
 * @property [key]
 */
data class AESKey(
    val key: String,
    val iv: String
)
