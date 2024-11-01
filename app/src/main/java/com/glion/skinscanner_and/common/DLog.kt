package com.glion.skinscanner_and.common

import android.util.Log

object DLog {
    const val TAG = "glion"

    fun d(msg: String) {
        Log.d(TAG, msg)
    }

    fun v(msg: String) {
        Log.v(TAG, msg)
    }

    fun i(msg: String) {
        Log.i(TAG, msg)
    }

    fun w(msg: String) {
        Log.w(TAG, msg)
    }

    fun e(msg: String, error: Exception?) {
        Log.e(TAG, msg, error)
    }
}