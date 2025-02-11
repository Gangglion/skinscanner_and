package com.glion.skinscanner_and.util.admob

import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.glion.skinscanner_and.util.LogUtil
import com.glion.skinscanner_and.util.Define
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.lang.ref.WeakReference

object AdmobUtil {
    private var activityRef: WeakReference<Activity>? = null // WeakReference 로 지정하여 Activity 종료 시 GC 의 대상이 되게 함
    private var listener: AdmobInterface? = null
    private var mRewardedAd: RewardedAd? = null

    private val fullscreenCallback = object : FullScreenContentCallback() {
        override fun onAdClicked() {
            LogUtil.d("Ad was Clicked")
        }

        override fun onAdDismissedFullScreenContent() {
            LogUtil.d("Ad dismissed fullscreen content")
            mRewardedAd = null
            listener?.adDismiss()
            listener = null // 사용이 종료된 listener 초기화
            loadAd(activityRef?.get()) // 광고가 종료되면 다시 로드
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            LogUtil.e("Ad failed to show fullscreen content")
            mRewardedAd = null
        }

        override fun onAdImpression() {
            LogUtil.d("Ad recorded an impression")
        }

        override fun onAdShowedFullScreenContent() {
            LogUtil.d("Ad showed fullscreen content.")
        }
    }

    fun loadAd(activity: Activity?, onLoaded : (() -> Unit)? = null) {
        if(activity == null) {
            throw IllegalArgumentException("Parameter \"activity\" can never be null")
        } else {
            activityRef = WeakReference(activity)

            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(activity, Define.AD_ID, adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    LogUtil.e("onAdFailedToLoad ::\n $loadAdError")
                    mRewardedAd = null
                    onLoaded?.invoke() // 광고 로드가 실패해도 바로 진행
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    LogUtil.d("Ad loaded successfully")
                    mRewardedAd = ad // 로드된 보상형 광고 저장
                    mRewardedAd?.fullScreenContentCallback = fullscreenCallback
                    onLoaded?.invoke() // 광고가 로드된 후 진행
                }
            })
        }
    }

    /**
     * 광고 listener 설정
     */
    fun setListener(admobListener: AdmobInterface) {
        listener = admobListener
    }

    /**
     * 광고 초기화 후 준비 완료되면 보여줌
     */
    fun showAd() {
        val activity = activityRef?.get()
        if(activity == null) {
            LogUtil.e("Activity is null. Cannot show Ad")
            return
        }

        if(mRewardedAd != null) {
            
            mRewardedAd?.show(activity) { rewardItem ->
                listener?.getReward(rewardItem.type)
            }
        } else {
            // 광고 로드가 실패한 경우
            listener?.adError()
        }
    }
}