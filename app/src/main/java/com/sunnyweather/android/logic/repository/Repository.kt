package com.sunnyweather.android.logic.repository

import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.flow.flow

object Repository {

    fun searchPlaces(query: String) = flow {
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }
}