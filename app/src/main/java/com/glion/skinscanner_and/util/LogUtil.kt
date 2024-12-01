package com.glion.skinscanner_and.util

import android.util.Log
import com.glion.skinscanner_and.BuildConfig

object LogUtil {
    const val TAG = "glion"
    private var isDev = BuildConfig.DEBUG

    fun v(msg: String, tr: Throwable? = null) {
        if(isDev) {
            Log.v(TAG, "${getLineNumber()}\n\uD83D\uDCAC $msg", tr)
        }
    }

    fun d(msg: String, tr: Throwable? = null) {
        if(isDev) {
            Log.d(TAG, "${getLineNumber()}\n\uD83D\uDC1E $msg", tr)
        }
    }

    fun i(msg: String, tr: Throwable? = null) {
        if(isDev) {
            Log.i(TAG, "${getLineNumber()}\nℹ\uFE0F $msg", tr)
        }
    }

    fun w(msg: String, tr: Throwable? = null) {
        if(isDev) {
            Log.w(TAG, "${getLineNumber()}\n⚠\uFE0F $msg", tr)
        }
    }

    fun e(msg: String, tr: Throwable? = null) {
        if(isDev) {
            Log.e(TAG, "${getLineNumber()}\n❌ $msg", tr)
        }
    }

    private fun getLineNumber(): String {
        val stackTrace = Thread.currentThread().stackTrace
        for (i in stackTrace.indices) {
            if (stackTrace[i].className != LogUtil::class.java.name) {
                if (i > 3) {
                    val fullClassName = stackTrace[i].className
                    val simpleClassName = fullClassName.substringAfterLast('.').substringBefore('$')
                    val lineNumber = stackTrace[i].lineNumber
                    return "$simpleClassName.kt:$lineNumber"
                }
            }
        }
        return "Unknown Class"
    }
}