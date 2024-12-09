package com.glion.skinscanner_and.ui.gallery

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.glion.skinscanner_and.BuildConfig
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.FragmentResizeBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.base.BaseFragment
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.Define
import com.glion.skinscanner_and.util.Utility
import com.glion.skinscanner_and.util.admob.AdmobInterface
import com.glion.skinscanner_and.util.admob.AdmobUtil
import com.glion.skinscanner_and.util.tflite.CancerQuantized
import com.glion.skinscanner_and.util.tflite.CancerType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ResizeFragment : BaseFragment<FragmentResizeBinding, MainActivity>(R.layout.fragment_resize), CancerQuantized.InferenceCallback, OnClickListener {
    private var earnedReward: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding) {
            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = withContext(Dispatchers.Main) {
                    Utility.getImageToBitmap(mContext, mContext.getString(R.string.saved_file_name))
                }
                cropView.setImageBitmap(bitmap)
                tvDoAnalyze.setOnClickListener(this@ResizeFragment)
            }
        }
    }

    /**
     * 촬영한 이미지로 분석 시작
     * @param [bitmap] 분석할 Bitmap
     */
    private fun startAnalyze(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.Main).launch {
            val cancerQuantized = CancerQuantized(mContext, this@ResizeFragment)
            cancerQuantized.processImage(bitmap).also {
                cancerQuantized.recognizeCancer(it)
            }
        }
    }

    override fun onResult(cancerType: CancerType?, percent: Int) {
        val adMobUtil = AdmobUtil(mParentActivity, object : AdmobInterface {
            override fun adDismiss() {
                if(BuildConfig.DEBUG) {
                    if(earnedReward == "coins") {
                        processIsCancer(cancerType, percent)
                    }
                } else {
                    if(mContext.getString(R.string.reward_type) == earnedReward) { // note : 얻은 보상 타입이 미리 지정한 보상 타입과 같은 경우, 화면 이동
                        processIsCancer(cancerType, percent)
                    }
                }
            }

            override fun getReward(rewardType: String) {
                earnedReward = rewardType
            }

            override fun adError() {
                processIsCancer(cancerType, percent)
            }
        })
        adMobUtil.showAd()
    }


    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.tv_do_analyze -> {
                val croppedImage = mBinding.cropView.getCroppedImage()!!
                Utility.saveBitmapInCache(croppedImage, mContext)
                startAnalyze(croppedImage)
                mLoadingDialog.show()
            }
        }
    }

    private fun processIsCancer(cancerType: CancerType?, percent: Int) {
        if(cancerType != null) {
            val resultCancer = when(cancerType) {
                CancerType.AKIEC -> mContext.getString(R.string.cancer_akiec)
                CancerType.BCC -> mContext.getString(R.string.cancer_bcc)
                CancerType.MEL -> mContext.getString(R.string.cancer_mel)
            }
            val bundle = Bundle().apply {
                putString(Define.RESULT, resultCancer)
                putInt(Define.VALUE, percent)
            }
            mLoadingDialog.dismiss()
            mParentActivity.changeFragment(ScreenType.Result, bundle)
        } else {
            mLoadingDialog.dismiss()
            val bundle = Bundle().apply {
                putString(Define.RESULT, mContext.getString(R.string.not_cancer))
            }
            mParentActivity.changeFragment(ScreenType.Result, bundle)
        }
    }
}