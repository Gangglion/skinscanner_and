package com.glion.skinscanner_and.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.ActivityMainBinding
import com.glion.skinscanner_and.ui.base.BaseActivity
import com.glion.skinscanner_and.ui.camera.CameraFragment
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.ui.find_dermatology.FindDermatologyFragment
import com.glion.skinscanner_and.ui.gallery.GalleryFragment
import com.glion.skinscanner_and.ui.gallery.ResizeFragment
import com.glion.skinscanner_and.ui.home.HomeFragment
import com.glion.skinscanner_and.ui.result.ResultFragment
import com.glion.skinscanner_and.util.Define
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private var mCurrentScreen = ScreenType.Home

    // 모델을 통해 나온 결과 MainActivity 에 저장
    var savedCancerResult: String? = null
    var savedPercent: Int = -1

    private val backPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when(mCurrentScreen) {
                ScreenType.Home -> {
                    finish()
                }
                ScreenType.Camera, ScreenType.Gallery, ScreenType.Resize -> {
                    changeFragment(ScreenType.Home)
                }
                ScreenType.Result -> {
                    changeFragment(ScreenType.Home)
                }
                ScreenType.Find -> {
                    val bundle = Bundle().apply {
                        putString(Define.RESULT, savedCancerResult)
                        putInt(Define.VALUE, savedPercent)
                    }
                    changeFragment(ScreenType.Result, bundle)
                }
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onBackPressedDispatcher.addCallback(backPressed)
    }

    fun changeFragment(type: ScreenType, bundle: Bundle? = null) {
        mCurrentScreen = type
        when(type) {
            ScreenType.Home -> {
                val fragment = HomeFragment()
                if(bundle != null) fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(binding.fcView.id, fragment).commit()
            }
            ScreenType.Camera -> {
                val fragment = CameraFragment()
                if(bundle != null) fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(binding.fcView.id, fragment).commit()
            }
            ScreenType.Gallery -> {
                val fragment = GalleryFragment()
                if(bundle != null) fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(binding.fcView.id, fragment).commit()
            }
            ScreenType.Resize -> {
                val fragment = ResizeFragment()
                if(bundle != null) fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(binding.fcView.id, fragment).commit()
            }
            ScreenType.Result -> {
                val fragment = ResultFragment()
                if(bundle != null) fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(binding.fcView.id, fragment).commit()
            }
            ScreenType.Find -> {
                val fragment = FindDermatologyFragment()
                if(bundle != null) fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(binding.fcView.id, fragment).commit()
            }
        }
    }
}