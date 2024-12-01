package com.glion.skinscanner_and.util.admob

import android.app.Activity
import com.glion.skinscanner_and.util.LogUtil
import com.glion.skinscanner_and.util.Define
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdmobUtil(
    private val activity: Activity,
    private val listener: AdmobInterface
) {
    private var mRewardedAd: RewardedAd? = null

    private val fullscreenCallback = object : FullScreenContentCallback() {
        override fun onAdClicked() {
            super.onAdClicked()
            LogUtil.d("Ad was Clicked")
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            LogUtil.d("Ad dismissed fullscreen content")
            mRewardedAd = null
            listener.adDismiss()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            super.onAdFailedToShowFullScreenContent(adError)
            LogUtil.e("Ad failed to show fullscreen content")
            mRewardedAd = null
        }

        override fun onAdImpression() {
            super.onAdImpression()
            LogUtil.d("Ad recorded an impression")
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            LogUtil.d("Ad showed fullscreen content.")
        }
    }

    /**
     * 광고 초기화 후 준비 완료되면 보여줌
     */
    fun showAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity, Define.AD_ID, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                LogUtil.e("onAdFailedToLoad ::\n $loadAdError")
                mRewardedAd = null
                listener.adError()
            }

            override fun onAdLoaded(ad: RewardedAd) {
                mRewardedAd = ad
                mRewardedAd?.fullScreenContentCallback = fullscreenCallback
                mRewardedAd!!.show(activity) { rewardItem ->
                    listener.getReward(rewardItem.type)
                }
            }
        })
    }
}