package com.glion.skinscanner_and.ui.camera

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.databinding.FragmentCameraBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.dialog.LoadingDialog
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.DLog
import com.glion.skinscanner_and.util.Define
import com.glion.skinscanner_and.util.Utility
import com.glion.skinscanner_and.util.tflite.CancerQuantized
import com.glion.skinscanner_and.util.tflite.CancerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : BaseFragment<FragmentCameraBinding, MainActivity>(R.layout.fragment_camera), OnClickListener, CancerQuantized.InferenceCallback {
    companion object {
        var isBackCamera = true
    }

    private var mImageCapture: ImageCapture? = null
    private lateinit var mCameraExecutor: ExecutorService
    private lateinit var mTempPhotoFile: File
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()

        with(mBinding) {
            btnClose.setOnClickListener(this@CameraFragment)
            btnChangeCamera.setOnClickListener(this@CameraFragment)
            btnCapture.setOnClickListener(this@CameraFragment)
            tvReCapture.setOnClickListener(this@CameraFragment)
            tvDoAnalyze.setOnClickListener(this@CameraFragment)
        }
        mLoadingDialog = LoadingDialog(mContext, mContext.getString(R.string.wait_for_process_image))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(mContext)
        mTempPhotoFile = File(mContext.applicationContext.cacheDir, mContext.getString(R.string.photo_file_name))
        cameraProviderFeature.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFeature.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(mBinding.previewCamera.surfaceProvider)
            }
            val cameraSelector = if(isBackCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
            mImageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, mImageCapture) // CameraProvider 에 ImageCapture 정보 넘긴다.
            } catch(e: Exception) {
                DLog.e("User Case Binding Failed", e)
            }
        }, ContextCompat.getMainExecutor(mContext))
        mBinding.clCamera.visibility = View.VISIBLE
        mBinding.clPreview.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            mBinding.btnClose.id -> {
                mParentActivity.changeFragment(ScreenType.Home)
            }
            mBinding.btnChangeCamera.id -> {
                changeCamera()
            }
            mBinding.btnCapture.id -> {
                Utility.getTakenPhotoFileInCache(mContext, mContext.getString(R.string.photo_file_name))?.delete() // 저장된 비트맵 이미지 제거
                takePhoto()
            }
            mBinding.tvReCapture.id -> {
                Utility.getTakenPhotoFileInCache(mContext, mContext.getString(R.string.photo_file_name))?.delete() // 저장된 비트맵 이미지 제거
                startCamera()
            }
            mBinding.tvDoAnalyze.id -> {
                mLoadingDialog.show()
                startAnalyze()
            }
        }
    }

    private fun takePhoto() {
        if(mImageCapture == null) return

        val outputOption = ImageCapture.OutputFileOptions.Builder(mTempPhotoFile).build()
        mImageCapture!!.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(mContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Glide.with(mContext).load(outputFileResults.savedUri).apply(
                        // 캐시에 저장된 이전 이미지를 재활용 하지 않도록 처리한다
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                    ).into(mBinding.ivPreview)
                    mBinding.clCamera.visibility = View.GONE
                    mBinding.clPreview.visibility = View.VISIBLE
                }

                override fun onError(exception: ImageCaptureException) {
                    mParentActivity.showToast(mContext.getString(R.string.fail_capture))
                    mLoadingDialog.dismiss()
                }
            }
        )
    }

    private fun changeCamera() {
        isBackCamera = !isBackCamera
        mParentActivity.changeFragment(ScreenType.Camera)
    }

    /**
     * 촬영한 이미지로 분석 시작
     */
    private fun startAnalyze() {
        CoroutineScope(Dispatchers.Main).launch {
            val cancerQuantized = CancerQuantized(mContext, this@CameraFragment)
            val bitmap: Bitmap? = Utility.getImageToBitmap(mContext, mContext.getString(R.string.photo_file_name))
            if(bitmap != null) {
                cancerQuantized.processImage(bitmap).also {
                    cancerQuantized.recognizeCancer(it)
                }
            } else {
                DLog.e("startAnalyze - 분석 실패", null)
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
}