package com.glion.skinscanner_and.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glion.skinscanner_and.data.api.repository.NetworkDatasource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val networkDatasource: NetworkDatasource
) : ViewModel() {
    private val _uiState = MutableStateFlow<SplashState>(SplashState.OnLoading)
    val uiState: StateFlow<SplashState> = _uiState

    fun checkVersion() {
        viewModelScope.launch {
            networkDatasource.getVersion()
                .onStart { _uiState.emit(SplashState.OnLoading) }
                .catch { e -> _uiState.emit(SplashState.OnError(e)) }
                .collect { flag ->
                    if(flag != null)
                        _uiState.emit(SplashState.OnUpdate(flag))
                    else
                        _uiState.emit(SplashState.OnError(null))
                }
        }
    }
}

sealed interface SplashState {
    data object OnLoading : SplashState
    data class OnUpdate(val flag: Int) : SplashState
    data object OnModelDownload : SplashState
    data class OnError(val error: Throwable?) : SplashState
}