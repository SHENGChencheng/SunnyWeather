package com.sunnyweather.android.ui.place

import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class PlaceViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _placeList = MutableStateFlow<List<Place>>(emptyList())
    val placeList: StateFlow<List<Place>> = _placeList.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val placeFlow = searchQuery.flatMapLatest { query ->
            Repository.searchPlaces(query)
        }

    fun searchPlaces(query: String) {
        _searchQuery.value = query
    }

    fun clearPlaceList() {
        _placeList.value = emptyList()
    }

    fun updatePlaceList(places: List<Place>) {
        _placeList.value = places
    }
}