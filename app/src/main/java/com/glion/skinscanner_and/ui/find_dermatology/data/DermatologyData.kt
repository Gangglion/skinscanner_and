package com.glion.skinscanner_and.ui.find_dermatology.data

/**
 * 피부과 데이터
 * @property [dermatologyTitle] 피부과 이름
 * @property [dermatologyUrl] 피부과 카카오맵 Url
 * @property [dermatologyNumber] 전화번호
 * @property [dermatologyAddr] 주소
 * @property [dermatologyDist] 떨어진 거리
 * @property [dermatologyLat] 위도
 * @property [dermatologyLng] 경도
 */
data class DermatologyData(
    val dermatologyTitle: String,
    val dermatologyUrl: String,
    val dermatologyNumber: String,
    val dermatologyAddr: String,
    val dermatologyDist: String,
    val dermatologyLat: Double,
    val dermatologyLng: Double
)
