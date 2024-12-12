package com.glion.skinscanner_and.util.extension

import android.content.Context
import android.content.pm.PackageManager

fun Context.checkPermission(permission: String) : Boolean {
    return this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}