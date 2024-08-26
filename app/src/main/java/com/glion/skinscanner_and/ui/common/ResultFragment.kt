package com.glion.skinscanner_and.ui.common

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.base.BaseFragment
import com.glion.skinscanner_and.databinding.FragmentResultBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.Define
import com.glion.skinscanner_and.util.Utility


class ResultFragment : BaseFragment<FragmentResultBinding, MainActivity>(R.layout.fragment_result), OnClickListener {
    private var mCancerResult: String? = null
    private var mCancerPercent: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            mCancerResult = it.getString(Define.RESULT)
            mCancerPercent = it.getInt(Define.VALUE)
        }
        mBinding.tvReCapture.setOnClickListener(this)
        setLayout()
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.tv_re_capture -> {
                Utility.deleteImage(mContext)
                mParentActivity.changeFragment(ScreenType.Home)
            }
        }
    }

    private fun setLayout() {
        with(mBinding) {
            Glide.with(mContext)
                .load(Utility.getSavedImage(mContext, mContext.getString(R.string.saved_file_name)))
                .apply(RequestOptions() // 캐시에 저장된 이전 이미지를 재활용 하지 않도록 처리한다
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                )
                .into(ivPhotoTaken)
            tvResult.text =  if(mCancerPercent != 0)
                mContext.getString(R.string.cancer_result_format).format(mCancerResult, mContext.getString(R.string.percent_format).format(mCancerPercent))
            else
                mContext.getString(R.string.not_cancer)
        }
    }
}