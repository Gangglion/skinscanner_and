package com.glion.skinscanner_and.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glion.skinscanner_and.data.tflite.data.AnalyzeResult
import com.glion.skinscanner_and.data.tflite.reppository.TfliteRepository
import com.glion.skinscanner_and.util.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val tfliteRepository: TfliteRepository
) : ViewModel() {
    private val _uiState = MutableSharedFlow<CameraUiState>(replay = 0)
    val uiState: SharedFlow<CameraUiState> = _uiState

    fun doCancerAnalyze() {
        viewModelScope.launch {
            tfliteRepository.cancerAnalyze()
                .onStart { _uiState.emit(CameraUiState.OnProcessing) }
                .catch { e ->
                    LogUtil.e("Error", e)
                    _uiState.emit(CameraUiState.OnError(e, "오류가 발생했습니다. 다시 시도해주세요"))
                }
                .collect { analyzeResult ->
                    if(analyzeResult == null) {
                        _uiState.emit(CameraUiState.OnError(msg = "이미지를 가져오지 못했습니다."))
                    } else {
                        _uiState.emit(CameraUiState.OnSuccess(analyzeResult))
                    }
                }
        }
    }
}

/**
 * 카메라 UI 상태
 * @property [OnLoading] 로딩중
 * @property [OnProcessing] 이미지 처리중
 * @property [OnError] 오류 발생
 * @property [OnSuccess] 성공
 */
sealed interface CameraUiState {
    data object OnLoading : CameraUiState
    data object OnProcessing: CameraUiState
    data class OnError(val thr: Throwable? = null, val msg: String) : CameraUiState
    data class OnSuccess(val analyzeResult: AnalyzeResult) : CameraUiState
}