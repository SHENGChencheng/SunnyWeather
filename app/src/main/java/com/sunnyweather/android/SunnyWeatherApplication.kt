package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        const val TOKEN = "pBdHk2NHZeg1bv2m"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}