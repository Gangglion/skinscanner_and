package com.glion.skinscanner_and.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.glion.skinscanner_and.AppVersion
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.ui.base.BaseActivity
import com.glion.skinscanner_and.util.LogUtil
import com.glion.skinscanner_and.util.Define
import com.glion.skinscanner_and.databinding.ActivityMainBinding
import com.glion.skinscanner_and.ui.camera.CameraFragment
import com.glion.skinscanner_and.ui.dialog.CommonDialog
import com.glion.skinscanner_and.ui.dialog.CommonDialogType
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.ui.find_dermatology.FindDermatologyFragment
import com.glion.skinscanner_and.ui.gallery.GalleryFragment
import com.glion.skinscanner_and.ui.gallery.ResizeFragment
import com.glion.skinscanner_and.ui.home.HomeFragment
import com.glion.skinscanner_and.ui.result.ResultFragment
import com.glion.skinscanner_and.util.Utility
import com.google.firebase.Firebase
import com.google.firebase.database.database
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

        // note : 앱 버전 체크
        checkVersion()
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

    /**
     * 앱 버전 체크
     */
    private fun checkVersion() {
        mLoadingDialog.show()
        Firebase.database.reference.get().addOnSuccessListener {
            mLoadingDialog.dismiss()
            val serverVersion: AppVersion = it.getValue(AppVersion::class.java)!!
            val flag = Utility.compareAppVersion(serverVersion.versionName, serverVersion.versionType)
            when(flag) {
                0 -> { /* note : 업데이트 하지 않음 */ }
                1 -> {
                    // note : 선택업데이트
                    showDialog(
                        dialogType = CommonDialogType.TwoButton,
                        title = mContext.getString(R.string.default_dialog_title),
                        contents = mContext.getString(R.string.update_dialog_contents),
                        leftBtnStr = mContext.getString(R.string.update_later),
                        rightBtnStr = mContext.getString(R.string.update_now),
                        listener = object : CommonDialog.DialogButtonClick {
                            override fun rightBtnClick() {
                                super.rightBtnClick()
                                Utility.goMarket(mContext)
                            }
                        }
                    )
                }
                2 -> {
                    // note : 강제업데이트
                    showDialog(
                        dialogType = CommonDialogType.OneButton,
                        title = mContext.getString(R.string.default_dialog_title),
                        contents = mContext.getString(R.string.update_force_dialog_contents),
                        singleBtnStr = mContext.getString(R.string.update_now),
                        listener = object : CommonDialog.DialogButtonClick {
                            override fun singleBtnClick() {
                                super.singleBtnClick()
                                Utility.goMarket(mContext)
                            }
                        }
                    )
                }
            }
        }.addOnFailureListener {
            mLoadingDialog.dismiss()
            LogUtil.e("Error Getting Data", it)
            // TODO : 파이어베이스 crashlytics 로그 전송 - fail get version in rtdb
        }
    }
}