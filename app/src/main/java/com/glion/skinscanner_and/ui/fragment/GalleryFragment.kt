package com.glion.skinscanner_and.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var mLoadingDialog: LoadingDialog

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
        mLoadingDialog = LoadingDialog(mContext, mContext.getString(R.string.wait_for_process_image))
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
        }, 2000L)
    }
}