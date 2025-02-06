package com.glion.skinscanner_and.ui.find_dermatology

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glion.skinscanner_and.data.api.data.DocumentData
import com.glion.skinscanner_and.data.api.repository.NetworkDatasource
import com.glion.skinscanner_and.data.location.repository.LocationRepository
import com.glion.skinscanner_and.util.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindDermatologyViewModel @Inject constructor(
    private val apiRepository: NetworkDatasource,
    private val locationRepository: LocationRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow<FindDermatologyState>(FindDermatologyState.Loading)
    val uiState: StateFlow<FindDermatologyState> = _uiState

    private var mSearchPage = 1
    private val mDataList: MutableList<DocumentData> = mutableListOf()

    /**
     * 기기에 저장된 마지막 위치 조회
     */
    fun getLastLocation() {
        viewModelScope.launch {
            locationRepository.getLastLocation()
                .collect { location ->
                    if(location != null) {
                        getDermatologyList(location.longitude.toString(), location.latitude.toString())
                    } else {
                        getCurrentLocation()
                    }
                }
        }
    }

    /**
     * 현재 위치 조회
     */
    fun getCurrentLocation() {
        viewModelScope.launch {
            locationRepository.getCurrentLocation()
                .collect { result ->
                    result.fold(
                        onSuccess = { location ->
                            refresh(location.longitude.toString(), location.latitude.toString())
                        },
                        onFailure = {
                            _uiState.emit(FindDermatologyState.Error("현재 위치 정보를 가져올 수 없습니다."))
                            refresh("126.9782038", "37.5665851")
                        }
                    )
                }
        }
    }

    /**
     * 가까운 피부과 조회
     */
    private fun getDermatologyList(x: String, y: String) {
        viewModelScope.launch {
            apiRepository.getDermatologyList(x = x, y = y, page = mSearchPage)
                .onStart { _uiState.emit(FindDermatologyState.Loading) }
                .catch { e ->
                    LogUtil.e("Error :: ", e)
                    _uiState.emit(FindDermatologyState.Error())
                }
                .collect { result ->
                    mDataList.addAll(result.dermatologyDataList)
                    if(result.isEnd) {
                        sortList()
                        _uiState.emit(FindDermatologyState.Success(mDataList))
                    } else {
                        mSearchPage++
                        getDermatologyList(x, y)
                    }
                }
        }
    }

    /**
     * 새로고침
     */
    fun refresh(x: String, y: String) {
        mDataList.clear()
        mSearchPage = 1
        getDermatologyList(x, y)
    }

    /**
     * 거리가 가까운 순으로 리스트 정렬
     */
    private fun sortList() {
        val comparator = compareBy<DocumentData> { it.dermatologyDist.toFloat() }
        mDataList.sortWith(comparator)
    }
}

sealed interface FindDermatologyState {
    data object Loading: FindDermatologyState
    data class Error(val message: String? = null) : FindDermatologyState
    data class Success(val dermatologyDataList: List<DocumentData>) : FindDermatologyState
}