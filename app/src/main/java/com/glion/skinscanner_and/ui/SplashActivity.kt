package com.glion.skinscanner_and.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.glion.skinscanner_and.AppVersion
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.ActivitySplashBinding
import com.glion.skinscanner_and.ui.base.BaseActivity
import com.glion.skinscanner_and.ui.dialog.CommonDialog
import com.glion.skinscanner_and.ui.dialog.CommonDialogType
import com.glion.skinscanner_and.util.LogUtil
import com.glion.skinscanner_and.util.RootCheck
import com.glion.skinscanner_and.util.Utility
import com.google.firebase.Firebase
import com.google.firebase.database.database

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 루팅 체크
        if(RootCheck(this).checkSu()) {
            showDialog(
                dialogType = CommonDialogType.OneButton,
                title = mContext.getString(R.string.notice),
                contents = mContext.getString(R.string.maybe_rooted_app),
                listener = object : CommonDialog.DialogButtonClick {
                    override fun singleBtnClick() {
                        super.singleBtnClick()
                        finish()
                    }
                }
            )
        } else {
            // note : 인터넷이 연결되어있을때만 앱 버전 체크
            if(Utility.checkNetworkStatus(mContext))
                checkVersion()
        }
    }

    /**
     * 앱 버전 체크
     */
    private fun checkVersion() {
        Firebase.database.reference.get().addOnSuccessListener {
            val serverVersion: AppVersion = it.getValue(AppVersion::class.java)!!
            val flag = Utility.compareAppVersion(serverVersion.versionName, serverVersion.versionType)
            when(flag) {
                0 -> { // note : 업데이트 하지 않음
                    startActivity(Intent(mContext, MainActivity::class.java))
                    finish()
                    // TODO : 서버와 키 교환 필요
                    // TODO : 모델 Hash 비교해서 다를 경우 다운로드 필요
                }
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
            LogUtil.e("Error Getting Data", it)
            startActivity(Intent(mContext, MainActivity::class.java))
            finish()
            // TODO : 파이어베이스 crashlytics 로그 전송 - fail get version in rtdb
            // TODO : 서버와 키 교환 필요
            // TODO : 모델 Hash 비교해서 다를 경우 다운로드 필요
        }
    }
}