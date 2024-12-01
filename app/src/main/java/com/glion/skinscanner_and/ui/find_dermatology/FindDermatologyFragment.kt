package com.glion.skinscanner_and.ui.find_dermatology

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.common.DLog
import com.glion.skinscanner_and.common.Define
import com.glion.skinscanner_and.ui.base.BaseFragment
import com.glion.skinscanner_and.util.Define
import com.glion.skinscanner_and.databinding.FragmentFindDermatologyBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.ui.find_dermatology.adapter.DermatologyListAdapter
import com.glion.skinscanner_and.ui.find_dermatology.data.DermatologyData
import com.glion.skinscanner_and.util.network.ApiClient
import com.glion.skinscanner_and.util.response.ResponseKeyword
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindDermatologyFragment : BaseFragment<FragmentFindDermatologyBinding, MainActivity>(R.layout.fragment_find_dermatology) {
    private lateinit var mListAdapter: DermatologyListAdapter
    private val mDataList: MutableList<DermatologyData> = mutableListOf()
    private var mSearchPage = 1
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    // TODO
    //  2. url 클릭 시 카카오맵 열리는 로직 불안함. 수정 필요
    //  3. 화면 상단 위치 갱신하는 코드 추가
    //  4. 네트워크 감지 리시버 추가
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext as Activity)
        mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if(location != null) {
                getDermatologyData(location.longitude.toString(), location.latitude.toString())
            } else { // 재부팅으로 인해 캐시에 저장된 위치가 없을 경우, 새로 위치 갱신 후 카카오맵 세팅
                getCurrentLocation()
            }
        }

        mBinding.btnTemp.setOnClickListener {
            mParentActivity.changeFragment(ScreenType.Home)
        }
    }

    /**
     * 피부과 데이터 가져오기 API 호출 함수s
     */
    private fun getDermatologyData(x: String, y: String) {
        mLoadingDialog.show()
        ApiClient.api.searchKeyword(Define.DERMATOLOGY, Define.DERMATOLOGY_TYPE, x, y, 3000, mSearchPage).enqueue(object : Callback<ResponseKeyword> {
            override fun onResponse(call: Call<ResponseKeyword>, response: Response<ResponseKeyword>) {
                DLog.d("Api Success")
                if(response.isSuccessful) {
                    val body = response.body()
                    if(body != null) {
                        for(item in body.documents) {
                            mDataList.add(
                                DermatologyData(
                                    dermatologyTitle = item.placeName,
                                    dermatologyUrl = item.placeUrl,
                                    dermatologyNumber = item.phone,
                                    dermatologyAddr = item.addressName,
                                    dermatologyDist = item.distance,
                                    dermatologyLat = item.y.toDouble(),
                                    dermatologyLng = item.x.toDouble()
                                )
                            )
                        }
                        if(!body.meta.is_end) {
                            mSearchPage++
                            getDermatologyData(x, y)
                        } else {
                            mLoadingDialog.dismiss()
                            sortList()
                            setAdapter()
                        }
                    } else {
                        // note : Api response body 가 null 일때의 처리
                        mLoadingDialog.dismiss()
                        mParentActivity.showToast(mContext.getString(R.string.network_error))
                    }
                } else {
                    // note : response 가 successful 이 아닐 경우 처리
                    mLoadingDialog.dismiss()
                    mParentActivity.showToast(mContext.getString(R.string.network_error))
                }
            }

            override fun onFailure(call: Call<ResponseKeyword>, throwable: Throwable) {
                DLog.e("Api fail", throwable as? Exception)
                mLoadingDialog.dismiss()
                mParentActivity.showToast(mContext.getString(R.string.network_error))
            }
        })
    }

    /**
     * 거리가 가까운 순으로 리스트 정렬
     */
    private fun sortList() {
        val comparator = compareBy<DermatologyData> { it.dermatologyDist.toFloat() }
        mDataList.sortWith(comparator)
    }

    private fun setAdapter() {
        mListAdapter = DermatologyListAdapter(mContext, mDataList)
        mBinding.rcList.adapter = mListAdapter
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        mFusedLocationClient.getCurrentLocation(createCurrentLocationRequest(), createCancellationToken())
            .addOnSuccessListener {
                getDermatologyData(it.longitude.toString(), it.latitude.toString())
            }
            .addOnFailureListener {
                Toast.makeText(mContext, "현재 위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                getDermatologyData("126.9782038", "37.5665851")
            }
    }

    /**
     * 현재 위치 가져올 수 있는 요청 Builder 생성 반환
     */
    private fun createCurrentLocationRequest() =
        CurrentLocationRequest.Builder()
            .setDurationMillis(10000)
            .setMaxUpdateAgeMillis(10000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

    /**
     * 현재 위치 가져오기 실패했을때의 토큰 반환
     */
    private fun createCancellationToken() : CancellationToken = object : CancellationToken() {
        override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
            return CancellationTokenSource().token
        }

        override fun isCancellationRequested(): Boolean {
            return false
        }
    }
}