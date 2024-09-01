package com.glion.skinscanner_and.ui.common

import android.os.Bundle
import android.view.View
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.databinding.FragmentFindDermatologyBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.DLog
import com.glion.skinscanner_and.util.network.ApiClient
import com.glion.skinscanner_and.util.response.Document
import com.glion.skinscanner_and.util.response.ResponseKeyword
import com.glion.skinscanner_and.util.response.ResponseSearch
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindDermatologyFragment : BaseFragment<FragmentFindDermatologyBinding, MainActivity>(R.layout.fragment_find_dermatology) {
    // TODO
    //  1. 위치 권한 허용 받기 - 허용 안했을 시 팝업 노출 및 사용 불가
    //  2. 현재 내 위치로 KakaoMap 세팅
    //  3. UI 어떤식으로 할지? 내 생각엔 리스트 띄워주고, 클릭하면 리스트 아래로 확장되서 병원 위치랑 전화걸기 정도 나오도록?
    //  3-1. 리스트에 표시해야 할 정보 - 내 위치에서 얼마나 떨어져있는지, 병원 이름, 주소, 전화걸기, 웹페이지 열기
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.btnTemp.setOnClickListener {
            mParentActivity.changeFragment(ScreenType.Home)
        }
        startMap()
    }

    override fun onResume() {
        super.onResume()
        ApiClient.api.searchAddress("안현로 35").enqueue(object : Callback<ResponseSearch> {
            override fun onResponse(p0: Call<ResponseSearch>, p1: Response<ResponseSearch>) {
                DLog.d("Api Success")
            }

            override fun onFailure(p0: Call<ResponseSearch>, e: Throwable) {
                DLog.d("Api fail")
            }
        })

        // temp : 테스트를 위해 집 주소 근처로 세팅 - 범위 3키로 고정
        ApiClient.api.searchKeyword("피부과", "HP8", "126.874799681274", "37.4675176060977", 3000).enqueue(object : Callback<ResponseKeyword> {
            override fun onResponse(p0: Call<ResponseKeyword>, p1: Response<ResponseKeyword>) {
                DLog.d("Api Success")
                // TODO : 리사이클러뷰 세팅, 클릭 시, 레이아웃 확장되며 지도 시작, 스크롤 금지, 확대/축소레벨 맞게 설정
            }

            override fun onFailure(p0: Call<ResponseKeyword>, p1: Throwable) {
                DLog.d("Api fail")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mBinding.kakaoMap.pause()
    }

    private fun startMap(item: Document? = null) {
        mBinding.kakaoMap.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    // 지도 API 가 정상적으로 종료될 떄 호출됨
                    DLog.d("지도 API 가 정상적으로 종료됨")
                }

                override fun onMapError(e: Exception?) {
                    // 인증 실패 및 지도 사용 중 에러 발생 시 호출 됨
                    DLog.e("인증 실패 및 지도 사용 중 에러 발생", e)
                }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(p0: KakaoMap) {
                    // 인증 API 가 정상적으로 실행될 때 호출됨
                    DLog.d("인증 API 정상적으로 실행 됨")
                }

                override fun getPosition(): LatLng {
                    return if(item == null) {
                        LatLng.from(37.467517, 126.874799)
                    } else {
                        LatLng.from(item.y.toDouble(), item.x.toDouble())
                    }
                }

                override fun getZoomLevel(): Int {
                    return 14
                }
            })
        mBinding.kakaoMap.resume()
    }
}