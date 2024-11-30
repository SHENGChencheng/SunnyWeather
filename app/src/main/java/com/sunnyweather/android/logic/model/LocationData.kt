package com.sunnyweather.android.logic.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(
    val lng: String,
    val lat: String,
    val placeName: String
) : Parcelable