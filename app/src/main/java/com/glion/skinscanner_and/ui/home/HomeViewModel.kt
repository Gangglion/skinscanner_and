package com.glion.skinscanner_and.ui.home

import androidx.lifecycle.ViewModel
import com.glion.skinscanner_and.data.datastore.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    dataStoreRepository: DataStoreRepository
) : ViewModel(){

}