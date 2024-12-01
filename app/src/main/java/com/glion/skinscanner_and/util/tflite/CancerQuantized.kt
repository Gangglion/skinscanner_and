package com.glion.skinscanner_and.util.tflite

import android.content.Context
import android.graphics.Bitmap
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.util.LogUtil
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.exp

class CancerQuantized(
    private val mContext: Context,
    private val callback: InferenceCallback
) {
    interface InferenceCallback {
        fun onResult(cancerType: CancerType?, percent: Int)
    }

    /**
     * (1, 260, 260, 3) uInt8
     */
    fun processImage(bitmap: Bitmap) : TensorImage {
        val imageProcessor = ImageProcessor.Builder().add(ResizeOp(260, 260, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)).build()
        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(bitmap)
        return imageProcessor.process(tensorImage)
    }

    fun recognizeCancer(tensorImage: TensorImage) {
        val input = tensorImage
        val output = arrayOf(FloatArray(4)) // 모델의 결과는 무조건 array 형태로 나온다. 반환되는 값이 1개라면 array내에 0번째 인덱스에만 값이 들어가있는 형태
        val tfliteModel = getModelInterpreter() // 모델 로드
        tfliteModel.run(input.buffer, output)
        analyzeResult(output[0])
    }

    private fun getModelInterpreter() : Interpreter {
        return Interpreter(loadModuleFile())
    }

    private fun loadModuleFile() : MappedByteBuffer {
        val fileDescriptor = mContext.assets.openFd(mContext.getString(R.string.model_name))
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun analyzeResult(modelResult: FloatArray) {
        val resultToMutableList = modelResult.toMutableList()
        val sigmoidCancerResult = valueToSigmoid(modelResult[0])
        LogUtil.d("결과값 : $sigmoidCancerResult")
        if(sigmoidCancerResult >= 0.5f) { // 암일 확률이 0.5이상일 경우
            resultToMutableList.removeAt(0)
            val maxValue = resultToMutableList.max()
            val cancerPercent = (sigmoidCancerResult * 100).toInt()
            when(resultToMutableList.indexOf(maxValue)) {
                0 -> callback.onResult(CancerType.AKIEC, cancerPercent)
                1 -> callback.onResult(CancerType.BCC, cancerPercent)
                2 -> callback.onResult(CancerType.MEL, cancerPercent)
            }
        } else {
            callback.onResult(null, 0)
        }
    }

    private fun valueToSigmoid(cancerPercent: Float) : Float {
        return  1/(1 + exp(-cancerPercent))
    }
}