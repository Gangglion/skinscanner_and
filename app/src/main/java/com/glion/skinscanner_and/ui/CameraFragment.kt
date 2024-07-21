package com.glion.skinscanner_and.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.databinding.FragmentCameraBinding
import com.glion.skinscanner_and.util.DLog

class CameraFragment : BaseFragment<FragmentCameraBinding, MainActivity>(R.layout.fragment_camera) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(mContext)

        cameraProviderFeature.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFeature.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(mBinding.viewFinder.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch(e: Exception) {
                DLog.e("User Case Binding Failed", e)
            }
        }, ContextCompat.getMainExecutor(mContext))
    }
}