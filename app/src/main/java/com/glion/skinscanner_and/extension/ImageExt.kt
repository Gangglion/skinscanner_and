package com.glion.skinscanner_and.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageProxy

fun ImageProxy.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get()
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}