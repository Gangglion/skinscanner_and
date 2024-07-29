package com.glion.skinscanner_and.ui.gallery

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
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

    /**
     * 갤러리 파일 선택 response
     */
    private val pickImage = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            // TODO : 가져온 이미지를 File 로 변경, 캐시에 저장. 분석 해야함
            // TODO : 분석하기, 다시 고르기 화면 세팅됨(카메라 찍었을때와 동일) 분석하기 누르면 분석 거쳐서 ResultFragment 로 이동하는것 동일
        } else {
            mParentActivity.showToast(mContext.getString(R.string.fail_get_image))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pickImage.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
//        mLoadingDialog = LoadingDialog(mContext, mContext.getString(R.string.wait_for_process_image))
//        startAnalyze()
    }

    // Temp
    private fun startAnalyze() {
//        CoroutineScope(Dispatchers.Main).launch {
//            val cancerQuantized = CancerQuantized(mContext, this@GalleryFragment)
//            val bitmap: Bitmap? = Utility.getImageToBitmap(mContext, EXAMPLE_1)
//            if(bitmap != null) {
//                cancerQuantized.processImage(bitmap).also {
//                    cancerQuantized.recognizeCancer(it)
//                }
//            } else {
//                Toast.makeText(mContext, mContext.getString(R.string.fail_analyze), Toast.LENGTH_SHORT).show()
//                mParentActivity.changeFragment(ScreenType.Home)
//            }
//        }
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