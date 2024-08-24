package com.glion.skinscanner_and.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.DisplayMetrics
import com.glion.skinscanner_and.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

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

    fun getSavedImage(mContext: Context, fileName: String) : File? {
        val file = File(mContext.getString(R.string.cache_path), fileName)

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

    /**
     * Uri to Bitmap
     */
    fun convertUriToBitmap(uri: Uri, context: Context): Bitmap? {
        try {
            return ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } catch(e: Exception) {
            DLog.e("Convert Bitmap Fail", e)
            return null
        }
    }

    /**
     * save bitmap in Cache
     */
    fun saveBitmapInCache(bitmap: Bitmap, mContext: Context) {
        deleteImage(mContext)
        val fileName = mContext.getString(R.string.saved_file_name)
        val file = File(mContext.getString(R.string.cache_path), fileName)
        try {
            file.createNewFile()
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()
        } catch(e: Exception) {
            DLog.e("File Saved Fail", e)
        }
    }

    /**
     * delete saved image in Cache
     */
    fun deleteImage(mContext: Context) {
        getSavedImage(mContext, mContext.getString(R.string.saved_file_name))?.delete()
    }
}