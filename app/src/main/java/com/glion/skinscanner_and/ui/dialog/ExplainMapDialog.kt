package com.glion.skinscanner_and.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.DialogExplainMapBinding

class ExplainMapDialog(
    context: Context
): Dialog(context, R.style.FullWindowDialog) {
    private val mBinding: DialogExplainMapBinding

    init {
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_explain_map, null, false)
        setContentView(mBinding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        with(mBinding) {
            ivClose.setOnClickListener {
                dismiss()
            }
        }
    }
}