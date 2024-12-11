package com.glion.skinscanner_and.util.extension

import android.content.Context
import android.util.DisplayMetrics

fun Int.toPx(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return (this / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT.toFloat())).toInt()
}

fun Float.toPx(context: Context): Float {
    val metrics = context.resources.displayMetrics
    return (this / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT.toFloat()))
}