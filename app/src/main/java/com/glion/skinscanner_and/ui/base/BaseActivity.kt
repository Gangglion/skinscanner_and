package com.glion.skinscanner_and.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.glion.skinscanner_and.ui.dialog.CommonDialog
import com.glion.skinscanner_and.ui.dialog.CommonDialogType
import com.glion.skinscanner_and.ui.dialog.LoadingDialog

abstract class BaseActivity<T: ViewDataBinding>(private val layoutResId: Int) : AppCompatActivity() {
    protected lateinit var binding: T
    protected lateinit var mContext: Context

    lateinit var mLoadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this
        mContext = this
        mLoadingDialog = LoadingDialog(mContext)
    }

    fun showToast(msg: String) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
    }

    fun showDialog(
        dialogType: CommonDialogType,
        title: String = "",
        contents: String = "",
        isCancelable: Boolean = false,
        leftBtnStr: String? = null,
        rightBtnStr: String? = null,
        singleBtnStr: String? = null,
        listener: CommonDialog.DialogButtonClick
    ) {
        CommonDialog(
            mContext = mContext,
            dialogType = dialogType,
            title = title,
            contents = contents,
            isCancelable = isCancelable,
            leftBtnStr = leftBtnStr,
            rightBtnStr = rightBtnStr,
            singleBtnStr = singleBtnStr,
            listener = listener
        ).show()
    }
}