package com.glion.skinscanner_and.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.DialogLoadingBinding

class LoadingDialog(
    mContext: Context
) : Dialog(mContext){
    private var mBinding: DialogLoadingBinding
    init {
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_loading, null, false)
        setContentView(mBinding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setMessage(msg: String) {
        mBinding.tvMessage.text = msg
    }
}