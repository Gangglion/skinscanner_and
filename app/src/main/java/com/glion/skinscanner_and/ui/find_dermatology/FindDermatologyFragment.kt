package com.glion.skinscanner_and.ui.find_dermatology

import android.os.Bundle
import android.view.View
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.common.DLog
import com.glion.skinscanner_and.common.Define
import com.glion.skinscanner_and.databinding.FragmentFindDermatologyBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.ui.find_dermatology.adapter.DermatologyListAdapter
import com.glion.skinscanner_and.ui.find_dermatology.data.DermatologyData
import com.glion.skinscanner_and.util.network.ApiClient
import com.glion.skinscanner_and.util.response.ResponseKeyword
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindDermatologyFragment : BaseFragment<FragmentFindDermatologyBinding, MainActivity>(R.layout.fragment_find_dermatology) {
    private lateinit var mListAdapter: DermatologyListAdapter
    private val mDataList: MutableList<DermatologyData> = mutableListOf()
    private var mSearchPage = 1
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
    }

    override fun onResume() {
        super.onResume()
        getDermatologyData()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun getDermatologyData() {
        // temp : 테스트를 위해 집 주소 근처로 세팅 - 범위 3키로 고정
        ApiClient.api.searchKeyword(Define.DERMATOLOGY, Define.DERMATOLOGY_TYPE, "126.874799681274", "37.4675176060977", 3000, mSearchPage).enqueue(object : Callback<ResponseKeyword> {
            override fun onResponse(call: Call<ResponseKeyword>, response: Response<ResponseKeyword>) {
                DLog.d("Api Success")
                if(response.isSuccessful) {
                    // TODO : 리사이클러뷰 세팅, 클릭 시, 레이아웃 확장되며 지도 시작, 스크롤 금지, 확대/축소레벨 맞게 설정
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
                            getDermatologyData()
                        } else {
                            sortList()
                            setAdapter()
                        }
                    } else {

                    }
                } else {

                }
            }

            override fun onFailure(p0: Call<ResponseKeyword>, p1: Throwable) {
                DLog.d("Api fail")
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
}