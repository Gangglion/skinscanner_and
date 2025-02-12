package com.glion.skinscanner_and.ui.find_dermatology

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.FragmentFindDermatologyBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.base.BaseFragment
import com.glion.skinscanner_and.ui.dialog.CommonDialog
import com.glion.skinscanner_and.ui.dialog.CommonDialogType
import com.glion.skinscanner_and.ui.dialog.FullScreenDialog
import com.glion.skinscanner_and.ui.find_dermatology.adapter.DermatologyListAdapter
import com.glion.skinscanner_and.util.NetworkConnectionCheck
import com.glion.skinscanner_and.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("MissingPermission")
class FindDermatologyFragment : BaseFragment<FragmentFindDermatologyBinding, MainActivity>(R.layout.fragment_find_dermatology) {
    private val findDermatologyViewModel: FindDermatologyViewModel by viewModels()
    private lateinit var mListAdapter: DermatologyListAdapter

    private var networkConnectionCheck: NetworkConnectionCheck? = null
    private var mNetworkWarnDialog: CommonDialog? = null
    private var networkStateCallback: NetworkConnectionCheck.NetworkStateCallback = object : NetworkConnectionCheck.NetworkStateCallback {
        override fun connect() {
            mParentActivity.runOnUiThread {
                if(mNetworkWarnDialog?.isVisible == true) {
                    mNetworkWarnDialog?.dismiss()
                }
            }
        }

        override fun disConnect() {
            mParentActivity.runOnUiThread {
                if(mNetworkWarnDialog?.isVisible == false) {
                    mNetworkWarnDialog?.show(mParentActivity.supportFragmentManager, "NetworkWarnDialog")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FullScreenDialog(R.drawable.ic_near_dermatology).show(mParentActivity.supportFragmentManager, "ExampleFindDermatologyDialog")

        with(mBinding) {
            btnTemp.setOnClickListener {
                findNavController().navigate(R.id.action_findDermatologyFragment_to_homeFragment)
            }
            swiperefreshlayout.setOnRefreshListener {
                findDermatologyViewModel.getCurrentLocation()
            }
            mListAdapter = DermatologyListAdapter(mContext, mutableListOf())
            rcList.adapter = mListAdapter
        }

        initNetworkCheck()
        observeDataListInfo()
    }

    override fun onResume() {
        super.onResume()
        mBinding.rcList.adapter?.notifyItemRangeChanged(0, mBinding.rcList.adapter!!.itemCount)
        findDermatologyViewModel.getLastLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkConnectionCheck?.unregister()
        networkConnectionCheck = null
    }

    private fun observeDataListInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                findDermatologyViewModel.uiState.collect { uiState ->
                    when(uiState) {
                        is FindDermatologyState.Loading -> { }
                        is FindDermatologyState.Success -> {
                            hideProgress()
                            mListAdapter.updateData(uiState.dermatologyDataList)
                            mBinding.swiperefreshlayout.isRefreshing = false
                        }
                        is FindDermatologyState.Error -> {
                            hideProgress()
                            if(uiState.message == null)
                                showToast(mContext.getString(R.string.network_error))
                            else
                                showToast(uiState.message)
                        }
                    }
                }
            }
        }
    }

    /**
     * 네트워크 연결상태 감지 초기화
     */
    private fun initNetworkCheck() {
        if(networkConnectionCheck == null) {
            networkConnectionCheck = NetworkConnectionCheck(mContext, networkStateCallback)
            networkConnectionCheck!!.register()
        }
        if(mNetworkWarnDialog == null) {
            mNetworkWarnDialog = CommonDialog(
                dialogType = CommonDialogType.OneButton,
                isDismiss = false,
                title = mContext.getString(R.string.notice),
                contents = mContext.getString(R.string.check_network_status),
                listener = object : CommonDialog.DialogButtonClick {
                    override fun singleBtnClick() {
                        if(Utility.checkNetworkStatus(mContext)) {
                            mNetworkWarnDialog?.dismiss()
                        }
                    }
                }
            )
        }
    }
}