package com.glion.skinscanner_and

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.glion.skinscanner_and.util.LogUtil
import com.glion.skinscanner_and.util.tflite.CancerQuantized
import com.glion.skinscanner_and.util.tflite.CancerType

import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CountDownLatch

import java.util.concurrent.Executors

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedDataSetTestUseTensorflowLight : CancerQuantized.InferenceCallback {
    private val latch = CountDownLatch(10014)
    private var imageName = ""
    
    @Test
    fun testModelInParallel() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val tfliteModel = CancerQuantized(appContext, this)

        val executor = Executors.newFixedThreadPool(4)
        val baseUrl = BuildConfig.DATA_SET_URL

        for(seq in 1..10015) {
            executor.submit {
                val imageUrl = String.format("%d.jpg", seq)
                imageName = imageUrl
                val fullUrl = "$baseUrl$imageUrl"
                val bitmap = fetchBitmapFromUrl(fullUrl)

                bitmap?.let {
                    tfliteModel.recognizeCancer(tfliteModel.processImage(it))
                    it.recycle()
                }
            }
        }

        executor.shutdown()
        latch.await()
        LogUtil.d("All Tasks completed")
    }

    override fun onResult(cancerType: CancerType?, percent: Int) {
        when(cancerType) {
            CancerType.AKIEC -> LogUtil.i("$imageName / 광선각화증 / $percent %")
            CancerType.BCC -> LogUtil.i("$imageName / 기저세포암 / $percent %")
            CancerType.MEL -> LogUtil.i("$imageName / 흑색종 / $percent %")
            else -> {  }
        }
        latch.countDown()
    }

    private fun fetchBitmapFromUrl(url: String) : Bitmap? {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch(e: Exception) {
            null
        }
    }
}