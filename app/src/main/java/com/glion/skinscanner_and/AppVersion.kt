package com.glion.skinscanner_and

data class AppVersion(
    val versionName: String = BuildConfig.VERSION_NAME,
    val versionType: Int = 0
)
