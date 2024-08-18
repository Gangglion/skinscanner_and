package com.glion.skinscanner_and

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class SkinScannerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}