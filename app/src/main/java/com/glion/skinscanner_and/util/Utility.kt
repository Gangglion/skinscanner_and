package com.glion.skinscanner_and.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object Utility{

    suspend fun getImageToBitmap(mContext: Context, fileName: String) : Bitmap?{
        return withContext(Dispatchers.IO) {
            val cacheDir = mContext.cacheDir
            val file = File(cacheDir, fileName)

            if(file.exists()) {
                try {
                    BitmapFactory.decodeFile(cacheDir.path + "/$fileName")
                } catch(e: Exception) {
                    DLog.e("getImageByteArrayFromCache Has Error", e)
                    null
                }
            } else {
                null
            }
        }
    }

    fun getTakenPhotoFileInCache(mContext: Context, fileName: String) : File? {
        val cacheDir = mContext.cacheDir
        val file = File(cacheDir, fileName)

        return if(file.exists()) {
            file
        } else {
            null
        }
    }

    /**
     * PxToDp
     */
    fun pxToDp(px: Float, context: Context) : Int {
        val metrics = context.resources.displayMetrics
        return (px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT.toFloat())).toInt()
    }
}