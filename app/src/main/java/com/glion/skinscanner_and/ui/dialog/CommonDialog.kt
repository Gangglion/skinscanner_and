package com.glion.skinscanner_and.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.DialogCommonBinding
import com.glion.skinscanner_and.extension.dialogResize
import com.glion.skinscanner_and.extension.getWindowHeight

/**
 * 공통 다이어로그 Class
 * @property [mContext] Context 객체
 * @property [dialogType] 다이어로그 타입 - OneButton / TwoButton
 * @property [title] 다이어로그 title
 * @property [contents] 다이어로그 contents
 * @property [isCancelable] 외부클릭으로 팝업 닫히는지 여부
 * @property [leftBtnStr] 왼쪽 버튼 텍스트
 * @property [rightBtnStr] 오른쪽 버튼 텍스트
 * @property [singleBtnStr] 단일버튼 텍스트
 * @property [listener] 버튼 클릭 리스너
 */
class CommonDialog(
    private val mContext: Context,
    private val dialogType: CommonDialogType = CommonDialogType.OneButton,
    private val title: String = "",
    private val contents: String = "",
    private val isCancelable: Boolean = false,
    private val leftBtnStr: String? = null,
    private val rightBtnStr: String? = null,
    private val singleBtnStr: String? = null,
    private val listener: DialogButtonClick
) : Dialog(mContext){
    private val mBinding: DialogCommonBinding

    interface DialogButtonClick {
        fun leftBtnClick() {}
        fun rightBtnClick() {}
        fun singleBtnClick() {}
    }

    init {
        setCanceledOnTouchOutside(isCancelable)
        setCancelable(isCancelable)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_common, null, false)
        setContentView(mBinding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.llDialogRoot.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val windowHeight = getWindowHeight()
                val dialogHeightPer = mBinding.llDialogRoot.height / windowHeight
                context.dialogResize(this@CommonDialog, 0.9f, dialogHeightPer)

                mBinding.llDialogRoot.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        setLayout()
    }

    private fun setLayout() {
        with(mBinding) {
            tvDialogTitle.text = title
            tvDialogContents.text = contents

            when (dialogType) {
                CommonDialogType.TwoButton -> {
                    llOneBtn.visibility = View.GONE
                    llTwoBtn.visibility = View.VISIBLE
                    btnLeft.apply {
                        text = leftBtnStr ?: mContext.getString(R.string.cancel)
                        setOnClickListener {
                            listener.leftBtnClick()
                            dismiss()
                        }
                    }
                    btnRight.apply {
                        text = rightBtnStr ?: mContext.getString(R.string.confirm)
                        setOnClickListener {
                            listener.rightBtnClick()
                            dismiss()
                        }
                    }
                }
                CommonDialogType.OneButton -> {
                    llOneBtn.visibility = View.VISIBLE
                    llTwoBtn.visibility = View.GONE
                    btnSingle.apply {
                        text = singleBtnStr ?: mContext.getString(R.string.confirm)
                        setOnClickListener {
                            listener.singleBtnClick()
                            dismiss()
                        }
                    }
                }
            }
        }
    }
}