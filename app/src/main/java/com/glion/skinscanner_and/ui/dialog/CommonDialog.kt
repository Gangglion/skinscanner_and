package com.glion.skinscanner_and.ui.dialog

import android.os.Bundle
import android.view.View
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.DialogCommonBinding
import com.glion.skinscanner_and.ui.base.BaseDialogFragment

/**
 * 공통 다이어로그 Class
 * @property [dialogType] 다이어로그 타입 - OneButton / TwoButton
 * @property [isDismiss] 버튼을 눌렀을때 다이어로그 dismiss 여부 - 기본값 true
 * @property [title] 다이어로그 title
 * @property [contents] 다이어로그 contents
 * @property [cancelable] 외부클릭으로 팝업 닫히는지 여부
 * @property [leftBtnStr] 왼쪽 버튼 텍스트
 * @property [rightBtnStr] 오른쪽 버튼 텍스트
 * @property [singleBtnStr] 단일버튼 텍스트
 * @property [listener] 버튼 클릭 리스너
 */
class CommonDialog(
    private val dialogType: CommonDialogType = CommonDialogType.OneButton,
    private val isDismiss: Boolean = true,
    private val title: String = "",
    private val contents: String = "",
    private val cancelable: Boolean = false,
    private val leftBtnStr: String? = null,
    private val rightBtnStr: String? = null,
    private val singleBtnStr: String? = null,
    private val listener: DialogButtonClick
) : BaseDialogFragment<DialogCommonBinding>(R.layout.dialog_common) {

    interface DialogButtonClick {
        fun leftBtnClick() {}
        fun rightBtnClick() {}
        fun singleBtnClick() {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = cancelable
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                            if(isDismiss)
                                dismiss()
                        }
                    }
                    btnRight.apply {
                        text = rightBtnStr ?: mContext.getString(R.string.confirm)
                        setOnClickListener {
                            listener.rightBtnClick()
                            if(isDismiss)
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
                            if(isDismiss)
                                dismiss()
                        }
                    }
                }
            }
        }
    }
}