package com.glion.skinscanner_and.util.response

/**
 * 주소 검색하기 API Result
 */
data class ResponseSearch(
    val documents: List<Document>,
    val meta: Meta
)