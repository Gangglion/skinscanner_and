package com.glion.skinscanner_and.ui

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.databinding.FragmentHomeBinding
import com.glion.skinscanner_and.ui.camera.CameraFragment
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.checkPermission

class HomeFragment : BaseFragment<FragmentHomeBinding, MainActivity>(R.layout.fragment_home), OnClickListener {

    private val responseCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted) {
            mParentActivity.changeFragment(ScreenType.Camera)
        } else {
            mParentActivity.showToast(mContext.getString(R.string.denied_permission))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.llOpenCamera.setOnClickListener(this@HomeFragment)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            mBinding.llOpenCamera.id -> {
                checkPermissionCamera()
            }

            mBinding.llOpenGallery.id -> {

            }
        }
    }

    private fun checkPermissionCamera() {
        when {
            mContext.checkPermission(Manifest.permission.CAMERA) -> {
                CameraFragment.isBackCamera = true
                mParentActivity.changeFragment(ScreenType.Camera)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(mParentActivity, Manifest.permission.CAMERA) -> {
                mParentActivity.showToast(mContext.getString(R.string.denied_permission))
            }
            else -> {
                responseCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }
}