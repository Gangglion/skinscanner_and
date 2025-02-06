package com.glion.skinscanner_and.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glion.skinscanner_and.data.tflite.data.AnalyzeResult
import com.glion.skinscanner_and.data.tflite.reppository.TfliteRepository
import com.glion.skinscanner_and.util.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResizeViewModel @Inject constructor(
    private val tfliteRepository: TfliteRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ResizeUiState>(ResizeUiState.OnLoading)
    val uiState: StateFlow<ResizeUiState> = _uiState

    fun doCancerAnalyze() {
        viewModelScope.launch {
            tfliteRepository.cancerAnalyze()
                .onStart { _uiState.emit(ResizeUiState.OnProcessing) }
                .catch { e ->
                    LogUtil.e("Error", e)
                    _uiState.emit(ResizeUiState.OnError(e, "오류가 발생했습니다. 다시 시도해주세요"))
                }
                .collect { analyzeResult ->
                    if(analyzeResult == null) {
                        _uiState.emit(ResizeUiState.OnError(msg = "이미지를 가져오지 못했습니다."))
                    } else {
                        _uiState.emit(ResizeUiState.OnSuccess(analyzeResult))
                    }
                }
        }
    }
}

sealed interface ResizeUiState {
    data object OnLoading : ResizeUiState
    data object OnProcessing: ResizeUiState
    data class OnError(val thr: Throwable? = null, val msg: String) : ResizeUiState
    data class OnSuccess(val analyzeResult: AnalyzeResult) : ResizeUiState
}