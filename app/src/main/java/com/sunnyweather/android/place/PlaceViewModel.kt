package com.sunnyweather.android.place

import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest

class PlaceViewModel : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    var placeList = ArrayList<Place>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val placeFlow = searchQuery
        .filter { it.isNotEmpty() }
        .flatMapLatest { query ->
            Repository.searchPlaces(query)
        }

    fun searchPlaces(query: String) {
        searchQuery.value = query
    }

}