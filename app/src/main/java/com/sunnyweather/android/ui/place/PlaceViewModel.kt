package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    private val _placeList = MutableStateFlow<List<Place>>(emptyList())
    val placeList: StateFlow<List<Place>> = _placeList.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val placeLiveData = searchLiveData.asFlow()
        .flatMapLatest { query ->
            Repository.searchPlaces(query)
        }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }
}