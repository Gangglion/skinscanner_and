package com.glion.skinscanner_and.ui.dialog

import android.os.Bundle
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.DialogLoadingBinding
import com.glion.skinscanner_and.ui.base.BaseDialogFragment

class LoadingDialog : BaseDialogFragment<DialogLoadingBinding>(R.layout.dialog_loading) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    fun setMessage(msg: String) {
        mBinding.tvMessage.text = msg
    }
}