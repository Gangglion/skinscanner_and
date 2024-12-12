package com.glion.skinscanner_and.ui.home

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.FragmentHomeBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.base.BaseFragment
import com.glion.skinscanner_and.ui.camera.CameraFragment
import com.glion.skinscanner_and.ui.dialog.CommonDialog
import com.glion.skinscanner_and.ui.dialog.CommonDialogType
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.Utility
import com.glion.skinscanner_and.util.extension.checkPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, MainActivity>(R.layout.fragment_home), OnClickListener {
    companion object {
        private const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES

        @RequiresApi(34)
        private const val READ_MEDIA_VISUAL_USER_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    }

    private val responseCameraPermission = registerForActivityResult(RequestPermission()) { isGranted ->
        if(isGranted) {
            mParentActivity.changeFragment(ScreenType.Camera)
        } else {
            showDeniedCameraPermissionDialog()
        }
    }

    private val requestGalleryPermission = registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
        if (isGranted) { // 권한을 허용했다면
            mParentActivity.changeFragment(ScreenType.Gallery)
        } else { // 권한을 허용하지 않았다면
            showDeniedMediaPermissionDialog()
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

            // TODO : 권한 허용 수정 필요
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
                showDeniedCameraPermissionDialog()
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
                showDeniedMediaPermissionDialog()
            }

            else -> { // 권한 허용 안되어있을 때
                requestGalleryPermission.launch(permission)
            }
        }
    }

    /**
     * 카메라 권한 허옹하지 않았을때 팝업
     */
    private fun showDeniedCameraPermissionDialog() {
        showDialog(
            dialogType = CommonDialogType.TwoButton,
            title = mContext.getString(R.string.default_dialog_title),
            contents = mContext.getString(R.string.permission_dialog_contents_camera),
            listener = object : CommonDialog.DialogButtonClick {
                override fun leftBtnClick() {
                    super.leftBtnClick()
                    showToast(mContext.getString(R.string.denied_permission_camera))
                }

                override fun rightBtnClick() {
                    super.rightBtnClick()
                    Utility.goSetting(mContext)
                }
            }
        )
    }

    /**
     * 사진 및 미디어 권한 허용하지 않았을때 팝업
     */
    private fun showDeniedMediaPermissionDialog() {
        showDialog(
            dialogType = CommonDialogType.TwoButton,
            title = mContext.getString(R.string.default_dialog_title),
            contents = mContext.getString(R.string.permission_dialog_contents_gallery),
            listener = object : CommonDialog.DialogButtonClick {
                override fun leftBtnClick() {
                    super.leftBtnClick()
                    showToast(mContext.getString(R.string.denied_permission_gallery))
                }

                override fun rightBtnClick() {
                    super.rightBtnClick()
                    Utility.goSetting(mContext)
                }
            }
        )
    }
}