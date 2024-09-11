package com.glion.skinscanner_and.util.response

import com.google.gson.annotations.SerializedName

/**
 * 카카오 restApi 키워드로 장소 검색하기 Response Document Object
 * @property [id] 장소 ID
 * @property [place_name] 장소명, 업체명
 * @property [category_name] 카테고리 이름
 * @property [category_group_code] 중요 카테고리만 그룹핑한 카테고리 그룹 코드
 * @property [category_group_name] 중요 카테고리만 그룹핑한 카테고리 그룹명
 * @property [phone] 전화번호
 * @property [address_name] 전체 지번 주소
 * @property [road_address_name] 전체 도로명 주소
 * @property [x] X 좌표값(경위도인 경우 Longitude)
 * @property [y] Y 좌표값(경위도인 경우 Latitude)
 * @property [place_url] 장소 웹페이지 URL
 * @property [distance] 중심좌표와의 거리(meter)
 */
data class Document(
    @SerializedName("id") val id: String,
    @SerializedName("place_name") val placeName: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("category_group_code") val categoryGroupCode: String,
    @SerializedName("category_group_name") val categoryGroupName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address_name") val addressName: String,
    @SerializedName("road_address_name") val roadAddressName: String,
    @SerializedName("x") val x: String,
    @SerializedName("y") val y: String,
    @SerializedName("place_url") val placeUrl: String,
    @SerializedName("distance") val distance: String,
)
