package com.sunnyweather.android.weather

import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.model.Location
import com.sunnyweather.android.logic.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class WeatherViewModel : ViewModel() {

    private val _location = MutableStateFlow(Location("", ""))
    val location: StateFlow<Location> = _location.asStateFlow()

    var locationLng = ""
    var locationLat = ""
    val placeName = ""

    @OptIn(ExperimentalCoroutinesApi::class)
    val weatherFlow = location.flatMapLatest { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    fun refreshWeather(lng: String, lat: String) {
        _location.value = Location(lng, lat)
    }

}