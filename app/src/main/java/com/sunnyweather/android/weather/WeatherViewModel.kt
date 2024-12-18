package com.sunnyweather.android.weather

import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.model.Location
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

class WeatherViewModel : ViewModel() {

    private val _location = MutableStateFlow(Place("", Location("", "") ,""))
    val location: StateFlow<Place> = _location.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val weatherFlow = location
        .filter { it.location.lng.isNotEmpty() && it.location.lat.isNotEmpty() }
        .flatMapLatest { location ->
        Repository.refreshWeather(location.location.lng, location.location.lat)
    }

    fun refreshWeather(
        lng: String = location.value.location.lng,
        lat: String = location.value.location.lat,
    ) {
        val newLocation = Location(lng, lat)
        _location.update {
            it.copy(
                location = newLocation,
                address = System.currentTimeMillis().toString()
            )
        }
    }

    fun initData(lng: String?, lat: String?, placeName: String?) {
        val newLocation = Location(lng.orEmpty(), lat.orEmpty())
        _location.update {
            it.copy(
                location = newLocation,
                name = placeName.orEmpty()
            )
        }
    }

    fun updateLocation(placeName: String, lng: String, lat: String) {
        _location.update {
            it.copy(
                name = placeName,
                location = Location(lng, lat),
            )
        }
    }

}