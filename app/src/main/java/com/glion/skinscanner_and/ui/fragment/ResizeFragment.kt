package com.glion.skinscanner_and.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.databinding.FragmentResizeBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.dialog.LoadingDialog
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.Define
import com.glion.skinscanner_and.util.Utility
import com.glion.skinscanner_and.util.tflite.CancerQuantized
import com.glion.skinscanner_and.util.tflite.CancerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResizeFragment : BaseFragment<FragmentResizeBinding, MainActivity>(R.layout.fragment_resize), CancerQuantized.InferenceCallback, OnClickListener {
    private lateinit var mLoadingDialog: LoadingDialog
    private var mSavedBitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLoadingDialog = LoadingDialog(mContext, mContext.getString(R.string.wait_for_process_image))
        with(mBinding) {
            CoroutineScope(Dispatchers.Main).launch {
                mSavedBitmap = Utility.getImageToBitmap(mContext, mContext.getString(R.string.saved_file_name))
                Glide.with(mContext).load(mSavedBitmap).apply(
                    // 캐시에 저장된 이전 이미지를 재활용 하지 않도록 처리한다
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                ).into(mBinding.ivPhoto)
            }
            ivArea.layoutParams.apply {
                width = Utility.pxToDp(600f, mContext)
                height = Utility.pxToDp(450f, mContext)
            }
            ivArea.requestLayout()
            tvDoAnalyze.setOnClickListener(this@ResizeFragment)
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

    private fun cropImage(originBitmap: Bitmap): Bitmap {
        with(mBinding) {
            val heightOriginal = ivPhoto.height
            val widthOriginal = ivPhoto.width
            val heightFrame = ivArea.height
            val widthFrame = ivArea.width
            val leftFrame = ivArea.left
            val topFrame = ivArea.top
            val heightReal = originBitmap.height
            val widthReal = originBitmap.width
            val widthFinal = widthFrame * widthReal / widthOriginal
            val heightFinal = heightFrame * heightReal / heightOriginal
            val leftFinal = leftFrame * widthReal / widthOriginal
            val topFinal = topFrame * heightReal / heightOriginal
            val croppedBitmap = Bitmap.createBitmap(originBitmap, leftFinal, topFinal, widthFinal, heightFinal)
            Utility.saveBitmapInCache(croppedBitmap, mContext)
            return croppedBitmap
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.tv_do_analyze -> {
                val cropBitmap = cropImage(mSavedBitmap!!)
                startAnalyze(cropBitmap)
                mLoadingDialog.show()
            }
        }
    }
}