package com.glion.skinscanner_and.ui.camera

import android.os.Bundle
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
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.DLog
import com.glion.skinscanner_and.util.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : BaseFragment<FragmentCameraBinding, MainActivity>(R.layout.fragment_camera), OnClickListener {
    companion object {
        var isBackCamera = true
        const val TAKEN_PHOTO_NAME = "skinImage.jpg"
    }

    private var mImageCapture: ImageCapture? = null
    private lateinit var mCameraExecutor: ExecutorService
    private lateinit var mTempPhotoFile: File

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(mContext)
        mTempPhotoFile = File(mContext.applicationContext.cacheDir, TAKEN_PHOTO_NAME)
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
                mTempPhotoFile.delete()
                takePhoto()
            }
            mBinding.tvReCapture.id -> {
                mTempPhotoFile.delete()
                startCamera()
            }
            mBinding.tvDoAnalyze.id -> {
                // TODO : 모델 들어오면 바이트 코드 넘겨서 분석 실시
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
                    getImageByteArrayAsync()

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
                }
            }
        )
    }

    /**
     * 이미지 촬영 완료 시, 백그라운드에서 바이트코드로 변환
     */
    private fun getImageByteArrayAsync() {
        CoroutineScope(Dispatchers.Main).launch {
            val byteArray = Utility.getImageByteArrayFromCache(mContext, TAKEN_PHOTO_NAME)
            if(byteArray != null) {
                DLog.i("코루틴 작업이 완료되고 반환되어 byteArray 가 null 이 아님")
            } else {
                DLog.w("Error")
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val byteArray = Utility.getImageByteArrayFromCache(mContext, TAKEN_PHOTO_NAME)
            if(byteArray != null)
                Utility.logByteArray(byteArray)
        }
    }

    private fun changeCamera() {
        isBackCamera = !isBackCamera
        mParentActivity.changeFragment(ScreenType.Camera)
    }
}