package com.glion.skinscanner_and.ui

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseActivity
import com.glion.skinscanner_and.databinding.ActivityMainBinding
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.checkPermission

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private var mCurrentScreen = ScreenType.Home

    private val backPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when(mCurrentScreen) {
                ScreenType.Home -> {
                    finish()
                }
                ScreenType.Camera, ScreenType.Gallery -> {
                    changeFragment(ScreenType.Home)
                }
                ScreenType.Result -> {
                    changeFragment(ScreenType.Home)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                // TODO : Move to Gallery Fragment??
            }
            ScreenType.Result -> {
                val fragment = ResultFragment()
                if(bundle != null) fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(binding.fcView.id, fragment).commit()
            }
        }
    }
}