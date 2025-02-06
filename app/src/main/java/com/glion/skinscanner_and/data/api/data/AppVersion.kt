package com.glion.skinscanner_and.data.api.data

import com.glion.skinscanner_and.BuildConfig

data class AppVersion(
    val versionName: String = BuildConfig.VERSION_NAME,
    val versionType: Int = 0
)
