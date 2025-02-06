package com.glion.skinscanner_and.data.tflite.reppository

import android.content.Context
import android.graphics.Bitmap
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.data.tflite.data.AnalyzeResult
import com.glion.skinscanner_and.util.Utility
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import javax.inject.Inject
import kotlin.math.exp

class TfliteRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tfliteModel: Interpreter
) : TfliteRepository {

    /**
     * TensorImage 로 전처리한 이미지를 통해 암 여부 판단
     * @param [processedImage] TensorImage 로 전처리된 TensorImage 객체
     */
    override suspend fun cancerAnalyze(): Flow<AnalyzeResult?> = flow {
        val bitmap = Utility.getImageToBitmap(context, context.getString(R.string.saved_file_name))
        if(bitmap == null) {
            emit(null)
        } else {
            val processedImage = processImage(bitmap)
            val output = arrayOf(FloatArray(4)) // 모델의 결과는 무조건 array 형태로 나온다. 반환되는 값이 1개라면 array내에 0번째 인덱스에만 값이 들어가있는 형태
            val tfliteModel = tfliteModel // 모델 로드
            tfliteModel.run(processedImage.buffer, output)
            val modelResult = output[0]
            val resultToMutableList = modelResult.toMutableList()
            val sigmoidCancerResult = valueToSigmoid(modelResult[0])

            if(sigmoidCancerResult >= 0.5f) { // 암일 확률이 0.5이상일 경우
                resultToMutableList.removeAt(0)
                val maxValue = resultToMutableList.max()
                val cancerPercent = (sigmoidCancerResult * 100).toInt()
                when(resultToMutableList.indexOf(maxValue)) {
                    0 -> emit(AnalyzeResult(context.getString(R.string.cancer_akiec), cancerPercent))
                    1 -> emit(AnalyzeResult(context.getString(R.string.cancer_bcc), cancerPercent))
                    2 -> emit(AnalyzeResult(context.getString(R.string.cancer_mel), cancerPercent))
                }
            } else {
                emit(AnalyzeResult(context.getString(R.string.not_cancer), -1))
            }
        }
    }.flowOn(Dispatchers.Default)

    /**
     * (1, 260, 260, 3) uInt8
     * 1장 이미지 가로 260 세로 260 3채널(RGB) uInt8 형식으로 변환
     */
    private fun processImage(bitmap: Bitmap) : TensorImage {
        val imageProcessor = ImageProcessor.Builder().add(ResizeOp(260, 260, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)).build()
        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(bitmap)
        return imageProcessor.process(tensorImage)
    }

    /**
     * 결과값에 시그모이드 처리
     */
    private fun valueToSigmoid(cancerPercent: Float) : Float {
        return  1/(1 + exp(-cancerPercent))
    }
}