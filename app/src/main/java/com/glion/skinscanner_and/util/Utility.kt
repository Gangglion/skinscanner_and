package com.glion.skinscanner_and.util

import android.content.Context
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

object Utility{

    suspend fun getImageByteArrayFromCache(mContext: Context, fileName: String) : ByteArray?{
        return withContext(Dispatchers.IO) {
            val cacheDir = mContext.cacheDir
            val file = File(cacheDir, fileName)

            if(file.exists()) {
                try {
                    val fileInputStream = FileInputStream(file)
                    val bytes = fileInputStream.readBytes()
                    fileInputStream.close()
                    bytes
                } catch(e: Exception) {
                    DLog.e("getImageByteArrayFromCache Has Error", e)
                    null
                }
            } else {
                null
            }
        }
    }

    /**
     * ByteArray 출력 확인용
     */
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun logByteArray(byteArray: ByteArray) {
        withContext(Dispatchers.IO) {
            CoroutineScope(Dispatchers.IO).launch {
                DLog.d("ByteArrayHex :: ${byteArray.toHexString(HexFormat.UpperCase).take(100)}")
            }
            CoroutineScope(Dispatchers.IO).launch {
                val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                DLog.d("ByteArrayBase64 :: ${base64String.take(100)}")
            }
        }
    }
}