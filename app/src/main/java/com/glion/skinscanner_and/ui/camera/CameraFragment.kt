package com.glion.skinscanner_and.ui.camera

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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glion.skinscanner_and.BuildConfig
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.FragmentCameraBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.base.BaseFragment
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.Define
import com.glion.skinscanner_and.util.LogUtil
import com.glion.skinscanner_and.util.Utility
import com.glion.skinscanner_and.util.admob.AdmobInterface
import com.glion.skinscanner_and.util.admob.AdmobUtil
import com.glion.skinscanner_and.util.tflite.CancerQuantized
import com.glion.skinscanner_and.util.tflite.CancerType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding, MainActivity>(R.layout.fragment_camera), OnClickListener, CancerQuantized.InferenceCallback {
    companion object {
        var isBackCamera = true
    }

    private var mImageCapture: ImageCapture? = null
    private lateinit var mCameraExecutor: ExecutorService
    private var earnedReward: String = ""

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
        cameraProviderFeature.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFeature.get()
            val preview = Preview.Builder()
                .build().also {
                    it.setSurfaceProvider(mBinding.previewCamera.surfaceProvider)
                }
            val cameraSelector = if(isBackCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
            mImageCapture = ImageCapture.Builder()
                .setTargetRotation(requireView().display.rotation)
                // 촬영된 이미지 비율 설정
                .apply {
                    val resolutionSelectorBuilder = ResolutionSelector.Builder().apply {
                        setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                    }
                    setResolutionSelector(resolutionSelectorBuilder.build())
                }
                .build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, mImageCapture) // CameraProvider 에 ImageCapture 정보 넘긴다.
            } catch(e: Exception) {
                LogUtil.e("User Case Binding Failed", e)
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
                Utility.deleteImage(mContext) // 저장된 비트맵 이미지 제거
                takePhoto()
            }
            mBinding.tvReCapture.id -> {
                Utility.deleteImage(mContext) // 저장된 비트맵 이미지 제거
                startCamera()
            }
            mBinding.tvDoAnalyze.id -> {
                mLoadingDialog.setMessage(mContext.getString(R.string.wait_for_process_image))
                mLoadingDialog.show()
                startAnalyze()
            }
        }
    }

    private fun takePhoto() {
        if(mImageCapture == null) return
        mImageCapture?.takePicture(ContextCompat.getMainExecutor(mContext), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = Bitmap.createBitmap(image.toBitmap(), 0, 0, image.width, image.height, Matrix().also{ it.setRotate(90F) }, true)
                val croppedBitmap = cropImage(bitmap)
                Utility.saveBitmapInCache(croppedBitmap, mContext)
                image.close()
                Glide.with(mContext).load(croppedBitmap).apply(
                    // 캐시에 저장된 이전 이미지를 재활용 하지 않도록 처리한다
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                ).into(mBinding.ivPreview)
                mBinding.clCamera.visibility = View.GONE
                mBinding.clPreview.visibility = View.VISIBLE
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                showToast(mContext.getString(R.string.fail_capture))
                mLoadingDialog.dismiss()
                mBinding.clCamera.visibility = View.VISIBLE
                mBinding.clPreview.visibility = View.GONE
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

    private fun cropImage(originBitmap: Bitmap): Bitmap {
        with(mBinding) {
            val heightOriginal = previewCamera.height
            val widthOriginal = previewCamera.width
            val heightFrame = vArea.height
            val widthFrame = vArea.width
            val leftFrame = vArea.left
            val topFrame = vArea.top
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

    /**
     * 촬영한 이미지로 분석 시작
     */
    private fun startAnalyze() {
        CoroutineScope(Dispatchers.Main).launch {
            val cancerQuantized = CancerQuantized(mContext, this@CameraFragment)
            val bitmap: Bitmap? = Utility.getImageToBitmap(mContext, mContext.getString(R.string.saved_file_name))
            if(bitmap != null) {
                cancerQuantized.processImage(bitmap).also {
                    cancerQuantized.recognizeCancer(it)
                }
            } else {
                LogUtil.e("startAnalyze - 분석 실패")
                with(mParentActivity) {
                    showToast("분석에 실패했습니다.")
                    mLoadingDialog.dismiss()
                    startCamera()
                }
            }
        }
    }

    /**
     * 모델 추론 왼료되면 광고 띄워줌
     */
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