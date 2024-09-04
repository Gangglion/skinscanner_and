package com.glion.skinscanner_and

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.glion.skinscanner_and.common.Define
import com.kakao.vectormap.KakaoMapSdk

class SkinScannerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        KakaoMapSdk.init(this, Define.KAKAO_MAP_KEY)
    }
}