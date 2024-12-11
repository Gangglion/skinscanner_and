package com.glion.skinscanner_and.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

/**
 * 네트워크 상태 감지 클래스
 * @param [mContext] Context 객체
 * @param [listener] NetworkStateCallback 리스너
 */
class NetworkConnectionCheck (
    mContext: Context,
    private val listener: NetworkStateCallback
) : ConnectivityManager.NetworkCallback() {
    private var networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()
    private var connectivityManager: ConnectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    interface NetworkStateCallback {
        fun connect()
        fun disConnect()
    }

    fun register() {
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        listener.connect()
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        listener.disConnect()
    }
}