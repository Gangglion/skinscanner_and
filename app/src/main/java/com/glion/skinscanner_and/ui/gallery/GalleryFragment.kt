package com.glion.skinscanner_and.ui.gallery

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.databinding.FragmentGalleryBinding
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

class GalleryFragment : BaseFragment<FragmentGalleryBinding, MainActivity>(R.layout.fragment_gallery), CancerQuantized.InferenceCallback {
    companion object {
        const val EXAMPLE_1 = "cancer.jpg"
        const val EXAMPLE_2 = "no_cancer.jpg"
    }
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLoadingDialog = LoadingDialog(mContext, mContext.getString(R.string.wait_for_process_image))
        startAnalyze()
    }

    // Temp
    private fun startAnalyze() {
        CoroutineScope(Dispatchers.Main).launch {
            val cancerQuantized = CancerQuantized(mContext, this@GalleryFragment)
            val bitmap: Bitmap? = Utility.getImageToBitmap(mContext, EXAMPLE_1)
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

    override fun onResult(cancerType: CancerType?, percent: Int) {
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