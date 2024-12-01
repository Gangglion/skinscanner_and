package com.glion.skinscanner_and.util.extension

import android.app.Dialog
import android.graphics.Point
import android.os.Build

fun Dialog.getWindowHeight(): Float {
    return if (Build.VERSION.SDK_INT < 30) {
        val display = window!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        size.y.toFloat()
    } else {
        val rect = window!!.windowManager.currentWindowMetrics.bounds
        rect.height().toFloat()
    }
}