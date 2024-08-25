package com.glion.skinscanner_and.ui.fragment

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : BaseFragment<FragmentCameraBinding, MainActivity>(R.layout.fragment_camera), OnClickListener {
    companion object {
        var isBackCamera = true
    }

    private var mImageCapture: ImageCapture? = null
    private lateinit var mCameraExecutor: ExecutorService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()

        with(mBinding) {
            btnClose.setOnClickListener(this@CameraFragment)
            btnChangeCamera.setOnClickListener(this@CameraFragment)
            btnCapture.setOnClickListener(this@CameraFragment)
            tvReCapture.setOnClickListener(this@CameraFragment)
            tvNext.setOnClickListener(this@CameraFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(mContext)
        cameraProviderFeature.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFeature.get()
            val preview = Preview.Builder()
                .build().also {
                    it.setSurfaceProvider(mBinding.previewCamera.surfaceProvider)
                }
            mBinding.previewCamera.scaleType = PreviewView.ScaleType.FIT_CENTER
            val cameraSelector = if(isBackCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
            mImageCapture = ImageCapture.Builder()
                .setTargetRotation(requireView().display.rotation)
                // 촬영된 이미지 비율 설정
                .apply {
                    val resolutionSelectorBuilder = ResolutionSelector.Builder().apply {
                        setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                    }
                    setResolutionSelector(resolutionSelectorBuilder.build())
                }
                .build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, mImageCapture) // CameraProvider 에 ImageCapture 정보 넘긴다.
            } catch(e: Exception) {
                DLog.e("User Case Binding Failed", e)
            }
        }, ContextCompat.getMainExecutor(mContext))
        mBinding.clCamera.visibility = View.VISIBLE
        mBinding.clCaptureCheck.visibility = View.GONE
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
                Utility.deleteImage(mContext) // 저장된 비트맵 이미지 제거
                takePhoto()
            }
            mBinding.tvReCapture.id -> {
                Utility.deleteImage(mContext) // 저장된 비트맵 이미지 제거
                startCamera()
            }
            mBinding.tvNext.id -> {
                mParentActivity.changeFragment(ScreenType.Resize)
            }
        }
    }

    private fun takePhoto() {
        if(mImageCapture == null) return
        mImageCapture?.takePicture(ContextCompat.getMainExecutor(mContext), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = image.toBitmap()
                val matrix = Matrix()
                matrix.setRotate(90F)
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                Utility.saveBitmapInCache(rotatedBitmap, mContext)
                image.close()
                Glide.with(mContext).load(rotatedBitmap).apply(
                    // 캐시에 저장된 이전 이미지를 재활용 하지 않도록 처리한다
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                ).into(mBinding.ivCaptureCheck)
                mBinding.clCamera.visibility = View.GONE
                mBinding.clCaptureCheck.visibility = View.VISIBLE
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                mParentActivity.showToast(mContext.getString(R.string.fail_capture))
                mBinding.clCamera.visibility = View.VISIBLE
                mBinding.clCaptureCheck.visibility = View.GONE
            }
        })
    }

    /**
     * 전후 카메라 전환
     */
    private fun changeCamera() {
        isBackCamera = !isBackCamera
        mParentActivity.changeFragment(ScreenType.Camera)
    }
}