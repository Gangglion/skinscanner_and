package com.glion.skinscanner_and.ui

import android.os.Bundle
import android.view.View
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.databinding.FragmentResultBinding
import com.glion.skinscanner_and.util.Define


class ResultFragment : BaseFragment<FragmentResultBinding, MainActivity>(R.layout.fragment_result) {
    private var mCancerResult: String? = null
    private var mCancerPercent: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            mCancerResult = it.getString(Define.RESULT)
            mCancerPercent = it.getInt(Define.VALUE)
        }
        mBinding.tvResult.text = mCancerResult
        if(mCancerPercent != 0)
            mBinding.tvPercent.text = mContext.getString(R.string.percent_format).format(mCancerPercent)
    }
}