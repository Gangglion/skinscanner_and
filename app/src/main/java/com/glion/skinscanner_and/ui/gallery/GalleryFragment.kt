package com.glion.skinscanner_and.ui.gallery

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.common.Define
import com.glion.skinscanner_and.databinding.FragmentGalleryBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.Utility
import com.glion.skinscanner_and.util.admob.AdmobInterface
import com.glion.skinscanner_and.util.admob.AdmobUtil
import com.glion.skinscanner_and.util.tflite.CancerQuantized
import com.glion.skinscanner_and.util.tflite.CancerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryFragment : BaseFragment<FragmentGalleryBinding, MainActivity>(R.layout.fragment_gallery), CancerQuantized.InferenceCallback {
    private var earnedReward: String = ""

    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when(result.resultCode) {
            Activity.RESULT_OK -> {
                val uri = result.data?.data
                if (uri != null) {
                    val bitmap = Utility.convertUriToBitmap(uri, mContext)
                    bitmap?.let {
                        Utility.saveBitmapInCache(bitmap, mContext)
                        // 갤러리에서 가져온 사진이  width : 600, height : 450 이 아닐경우 리사이즈
                        if(bitmap.width > 600 || bitmap.height > 450) {
                            mParentActivity.changeFragment(ScreenType.Resize)
                        } else{
                            mLoadingDialog.setMessage(mContext.getString(R.string.wait_for_process_image))
                            mLoadingDialog.show()
                            startAnalyze(it.copy(Bitmap.Config.ARGB_8888, true))
                        }
                    }
                } else {
                    with(mParentActivity) {
                        showToast(mContext.getString(R.string.fail_get_image))
                        mLoadingDialog.dismiss()
                        changeFragment(ScreenType.Home)
                    }
                }
            }
            else -> {
                with(mParentActivity){
                    mLoadingDialog.dismiss()
                    changeFragment(ScreenType.Home)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        galleryResultLauncher.launch(intent)
    }

    private fun startAnalyze(selectedImage: Bitmap?) {
        CoroutineScope(Dispatchers.Main).launch {
            val cancerQuantized = CancerQuantized(mContext, this@GalleryFragment)
            val bitmap = selectedImage
            if(bitmap != null) {
                cancerQuantized.processImage(bitmap).also {
                    cancerQuantized.recognizeCancer(it)
                }
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.fail_analyze), Toast.LENGTH_SHORT).show()
                mParentActivity.changeFragment(ScreenType.Home)
            }
        }
    }

    /**
     * 모델 추론이 끝나고 결과가 보여지기 전 광고 시청
     */
    override fun onResult(cancerType: CancerType?, percent: Int) {
        val adMobUtil = AdmobUtil(mParentActivity, object : AdmobInterface {
            override fun adDismiss() {
                if(mContext.getString(R.string.reward_type) == earnedReward) { // note : 얻은 보상 타입이 미리 지정한 보상 타입과 같은 경우, 화면 이동
                    processIsCancer(cancerType, percent)
                }
            }

            override fun getReward(rewardType: String) {
                earnedReward = rewardType
            }
        })
        adMobUtil.showAd()
    }

    /**
     * 광고 시청 이후, 암인지 이난지 결과를 보여주는 화면으로 이동
     */
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