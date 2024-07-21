package com.glion.skinscanner_and.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import com.glion.skinscanner_and.R

class LoadingDialog(
    private val mContext: Context,
    private val message: String
) : Dialog(mContext){

    init {
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        setContentView(R.layout.dialog_loading)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tvMessage = findViewById<AppCompatTextView>(R.id.tv_message)
        tvMessage.text = message
    }
}