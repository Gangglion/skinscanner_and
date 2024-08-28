package com.glion.skinscanner_and.ui.common

import android.os.Bundle
import android.view.View
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.databinding.FragmentFindDermatologyBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.enums.ScreenType

class FindDermatologyFragment : BaseFragment<FragmentFindDermatologyBinding, MainActivity>(R.layout.fragment_find_dermatology) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO :  카카오 지도 API 연결해서 가까운 피부과 검색 및 리스트업 시켜주기 - 디자인 생각해보기

        mBinding.btnTemp.setOnClickListener {
            mParentActivity.changeFragment(ScreenType.Home)
        }
    }
}