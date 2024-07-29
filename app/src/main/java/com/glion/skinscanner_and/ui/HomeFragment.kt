package com.glion.skinscanner_and.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.databinding.FragmentHomeBinding
import com.glion.skinscanner_and.ui.camera.CameraFragment
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.checkPermission

class HomeFragment : BaseFragment<FragmentHomeBinding, MainActivity>(R.layout.fragment_home), OnClickListener {
    companion object {
        private const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES

        @RequiresApi(34)
        private const val READ_MEDIA_VISUAL_USER_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    }

    private val responseCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted) {
            mParentActivity.changeFragment(ScreenType.Camera)
        } else {
            mParentActivity.showToast(mContext.getString(R.string.denied_permission))
        }
    }

    private val requestGalleryPermission = registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
        if (isGranted) { // 권한을 허용했다면
            mParentActivity.changeFragment(ScreenType.Gallery)
        } else { // 권한을 허용하지 않았다면
            mParentActivity.showToast(mContext.getString(R.string.denied_gallery))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.llOpenCamera.setOnClickListener(this@HomeFragment)
        mBinding.llOpenGallery.setOnClickListener(this@HomeFragment)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            mBinding.llOpenCamera.id -> {
                checkPermissionCamera()
            }

            mBinding.llOpenGallery.id -> {
                when (Build.VERSION.SDK_INT) {
                    Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                        handleClickOpenGallery(READ_MEDIA_VISUAL_USER_SELECTED)
                    }

                    Build.VERSION_CODES.TIRAMISU -> {
                        handleClickOpenGallery(READ_MEDIA_IMAGES)
                    }

                    else -> {
                        handleClickOpenGallery(READ_EXTERNAL_STORAGE)
                    }
                }
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

    private fun handleClickOpenGallery(permission: String) {
        when {
            mContext.checkPermission(permission) -> {
                mParentActivity.changeFragment(ScreenType.Gallery)
            }

            shouldShowRequestPermissionRationale(permission) -> {
                mParentActivity.showToast(mContext.getString(R.string.denied_permission))
            }

            else -> { // 권한 허용 안되어있을 때
                requestGalleryPermission.launch(permission)
            }
        }
    }
}