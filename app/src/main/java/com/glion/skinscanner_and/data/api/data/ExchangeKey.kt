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
 * @property [isSuccess] 성공 여부
 * @property [message] 메세지
 * @property [data] key\niv 형태의 암호화 된 string
 */
data class ResponseExchangeKey(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: AESKey,
)

data class AESKey(
    val key: String,
    val iv: String
)
