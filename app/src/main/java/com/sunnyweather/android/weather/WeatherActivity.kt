package com.sunnyweather.android.weather

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.logic.model.LocationData
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import com.sunnyweather.android.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }

    private lateinit var _binding: ActivityWeatherBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        _binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val locationData = intent.getParcelableExtra<LocationData>("location_data")
        locationData?.let {
            viewModel.initData(it.lng, it.lat, it.placeName)
        }
        lifecycleScope.launch {
            viewModel.weatherFlow.collect { result ->
                val weather = result.getOrNull()
                if (weather != null) {
                    showWeatherInfo(weather)
                } else {
                    showToast("无法成功获取天气信息")
                    result.exceptionOrNull()?.printStackTrace()
                }
                binding.swipeRefresh.isRefreshing = false
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                binding.swipeRefresh.isRefreshing = true
                delay(500)
                viewModel.refreshWeather()
            }
        }
        binding.nowLayout.navBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showWeatherInfo(weather: Weather) {
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局
        binding.nowLayout.apply {
            placeName.text = viewModel.location.value.name
            currentTemp.text = "${realtime.temperature.toInt()} ℃"
            currentAQI.text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
            currentSky.text = getSky(realtime.skycon).info
            nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        }
        // 填充forecast.xml布局
        binding.forecastLayout.apply {
            forecastContentLayout.removeAllViews()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val days = daily.skycon.size
            for (i in 0 until days) {
                val skycon = daily.skycon[i]
                val temperature = daily.temperature[i]
                val view = LayoutInflater.from(this@WeatherActivity).inflate(R.layout.forecast_item,
                    forecastContentLayout, false)
                with(view) {
                    findViewById<TextView>(R.id.dateInfo).text = simpleDateFormat.format(skycon.date)
                    findViewById<ImageView>(R.id.skyIcon).setImageResource(getSky(skycon.value).icon)
                    findViewById<TextView>(R.id.skyInfo).text = getSky(skycon.value).info
                    findViewById<TextView>(R.id.temperatureInfo).text = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
                }
                forecastContentLayout.addView(view)
            }
        }
        // 填充life_index.xml布局
        binding.lifeIndexLayout.apply {
            coldRiskText.text = daily.lifeIndex.coldRisk[0].desc
            dressingText.text = daily.lifeIndex.dressing[0].desc
            ultravioletText.text = daily.lifeIndex.ultraviolet[0].desc
            carWashingText.text = daily.lifeIndex.carWashing[0].desc
        }
        binding.weatherLayout.visibility = View.VISIBLE
    }
}