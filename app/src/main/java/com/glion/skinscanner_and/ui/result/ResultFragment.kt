package com.glion.skinscanner_and.ui.result

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
import com.glion.skinscanner_and.common.Define
import com.glion.skinscanner_and.util.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ResultFragment : BaseFragment<FragmentResultBinding, MainActivity>(R.layout.fragment_result), OnClickListener {
    private var mCancerResult: String? = null
    private var mCancerPercent: Int = -1

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
                    mParentActivity.changeFragment(ScreenType.Find)
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
}