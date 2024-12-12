package com.glion.skinscanner_and.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.DialogFullScreenBinding
import com.glion.skinscanner_and.ui.base.BaseDialogFragment

class FullScreenDialog(
    @DrawableRes private val imageRes: Int
) : BaseDialogFragment<DialogFullScreenBinding>(R.layout.dialog_full_screen) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding) {
            ivImage.setImageDrawable(AppCompatResources.getDrawable(mContext, imageRes))
            ivClose.setOnClickListener { dismiss() }
        }
    }
}