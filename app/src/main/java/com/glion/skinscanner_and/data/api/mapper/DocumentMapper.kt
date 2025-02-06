package com.glion.skinscanner_and.data.api.mapper

import com.glion.skinscanner_and.data.api.data.Document
import com.glion.skinscanner_and.data.api.data.DocumentData

fun Document.toData() = DocumentData(
    dermatologyTitle = this.placeName,
    dermatologyUrl = this.placeUrl,
    dermatologyNumber = this.phone,
    dermatologyAddr = this.addressName,
    dermatologyDist = this.distance,
    dermatologyLat = this.y.toDouble(),
    dermatologyLng = this.x.toDouble()
)