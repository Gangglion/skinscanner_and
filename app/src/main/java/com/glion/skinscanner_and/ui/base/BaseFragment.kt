package com.glion.skinscanner_and.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.glion.skinscanner_and.ui.dialog.CommonDialog
import com.glion.skinscanner_and.ui.dialog.CommonDialogType
import com.glion.skinscanner_and.ui.dialog.LoadingDialog

abstract class BaseFragment<T: ViewDataBinding, A: AppCompatActivity>(private val layoutResId: Int) : Fragment() {
    protected lateinit var mBinding: T
    protected lateinit var mParentActivity: A
    protected lateinit var mContext: Context

    private var mToast: Toast? = null

    lateinit var mLoadingDialog: LoadingDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mParentActivity = requireActivity() as A
        mLoadingDialog = LoadingDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return mBinding.root
    }

    fun showToast(msg: String) {
        mToast?.cancel()
        mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
        mToast?.show()
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
            dialogType = dialogType,
            title = title,
            contents = contents,
            cancelable = isCancelable,
            leftBtnStr = leftBtnStr,
            rightBtnStr = rightBtnStr,
            singleBtnStr = singleBtnStr,
            listener = listener
        ).show(mParentActivity.supportFragmentManager, "CommonDialog")
    }

    fun showProgress() {
        mLoadingDialog.show(mParentActivity.supportFragmentManager, "ProgressDialog")
    }

    fun hideProgress() {
        if(mLoadingDialog.isVisible) {
            mLoadingDialog.dismiss()
        }
    }
}