package com.glion.skinscanner_and.ui.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment<T: ViewDataBinding>(private val layoutResId: Int) : DialogFragment() {
    lateinit var mContext: Context
    lateinit var mBinding: T

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply{
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Dialog 의 커스텀 배경을 지정하기 위함(Radius 등)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // window 자체를 match_parent 로 지정함으로써 화면 가로에 꽉 차는 팝업 생성 가능
        }
    }
}