package com.glion.skinscanner_and.ui.result

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.common.Define
import com.glion.skinscanner_and.databinding.FragmentResultBinding
import com.glion.skinscanner_and.extension.checkPermission
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.dialog.CommonDialog
import com.glion.skinscanner_and.ui.dialog.CommonDialogType
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.Utility
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ResultFragment : BaseFragment<FragmentResultBinding, MainActivity>(R.layout.fragment_result), OnClickListener {
    companion object {
        const val LOCATION_RESULT_CODE = 100
    }

    private var mCancerResult: String? = null
    private var mCancerPercent: Int = -1
    private var requestPermissionLocation = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // 권한 응답 처리
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if(it[requestPermissionLocation[0]]!! && it[requestPermissionLocation[1]]!!) { // 권한을 둘다 허용
            checkLocationSetting()
        } else { // 하나라도 권한을 허용하지 않았다면
            showDialog(
                dialogType = CommonDialogType.TwoButton,
                title = mContext.getString(R.string.default_dialog_title),
                contents = mContext.getString(R.string.permission_dialog_contents_location),
                listener = object : CommonDialog.DialogButtonClick {
                    override fun rightBtnClick() {
                        super.rightBtnClick()
                        Utility.goSetting(mContext)
                    }
                }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            mCancerResult = it.getString(Define.RESULT)
            mCancerPercent = it.getInt(Define.VALUE, -1)
        }
        // 뒤로가기를 위한 데이터 저장
        with(mParentActivity) {
            savedCancerResult = mCancerResult
            savedPercent = mCancerPercent
        }
        mBinding.tvNext.setOnClickListener(this)
        setLayout()
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.tv_next -> {
                if(mCancerPercent != -1) { // 암일 경우
                    // TODO : 권한 체크 이후, 권한을 가지고 있을 떄 화면 이동. 권한이 없으면 Toast 띄워줌.
                    handleClickFindDermatology()
                } else { // 암이 아닐 경우
                    Utility.deleteImage(mContext)
                    mParentActivity.changeFragment(ScreenType.Home)
                }
            }
        }
    }

    private fun setLayout() {
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = Utility.getImageToBitmap(mContext, mContext.getString(R.string.saved_file_name))
            with(mBinding) {
                Glide.with(mContext)
                    .load(bitmap)
                    .apply(RequestOptions() // 캐시에 저장된 이전 이미지를 재활용 하지 않도록 처리한다
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                    )
                    .into(ivPhotoTaken)
                if(mCancerPercent != -1) {
                    tvResult.text = mContext.getString(R.string.cancer_result_format).format(mCancerResult, mContext.getString(R.string.percent_format).format(mCancerPercent))
                    tvNext.text = mContext.getString(R.string.find_near_dermatology)
                } else {
                    tvResult.text = mContext.getString(R.string.not_cancer)
                    tvNext.text = mContext.getString(R.string.go_main)
                }
            }
        }
    }

    /**
     * 가까운 피부과 찾기 클릭 시 위치 권한 체크, 권한 허용 되어 있을 시, 위치 활성화 여부 체크
     */
    private fun handleClickFindDermatology() {
        val rejectedPermission: MutableList<String> = mutableListOf()
        for(pm in requestPermissionLocation) {
            when {
                mContext.checkPermission(pm) -> {  }
                shouldShowRequestPermissionRationale(pm) -> {
                    rejectedPermission.add(pm)
                }
                else -> {
                    rejectedPermission.add(pm)
                }
            }
        }

        if(rejectedPermission.isNotEmpty()) {
            requestPermissionLauncher.launch(requestPermissionLocation)
        } else {
            checkLocationSetting()
        }
    }

    /**
     * 위치 활성화 여부 체크
     */
    private fun checkLocationSetting() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
        val locationSettingRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val client: SettingsClient = LocationServices.getSettingsClient(mParentActivity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(locationSettingRequest)
        task.addOnSuccessListener {
            mParentActivity.runOnUiThread {
                mParentActivity.changeFragment(ScreenType.Find)
            }
        }
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(mParentActivity, LOCATION_RESULT_CODE)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("shhan", sendEx.message.toString(), sendEx)
                }
            }
        }
    }

    /**
     * 위치 활성화 콜백
     */
    // fixme : 교체 예정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            LOCATION_RESULT_CODE -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        mParentActivity.changeFragment(ScreenType.Find)
                    }
                    Activity.RESULT_CANCELED -> {
                        checkLocationSetting()
                        Toast.makeText(mContext, "기기 위치 기능을 사용 설정 해 주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}