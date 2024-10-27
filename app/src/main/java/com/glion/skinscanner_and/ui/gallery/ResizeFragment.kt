package com.glion.skinscanner_and.ui.gallery

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.common.Define
import com.glion.skinscanner_and.databinding.FragmentResizeBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.Utility
import com.glion.skinscanner_and.util.tflite.CancerQuantized
import com.glion.skinscanner_and.util.tflite.CancerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResizeFragment : BaseFragment<FragmentResizeBinding, MainActivity>(R.layout.fragment_resize), CancerQuantized.InferenceCallback, OnClickListener {

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
        Handler(Looper.getMainLooper()).postDelayed({
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
        },2000L)
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
}