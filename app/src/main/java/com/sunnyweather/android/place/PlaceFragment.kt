package com.sunnyweather.android.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.databinding.FragmentPlaceBinding
import com.sunnyweather.android.logic.model.LocationData
import com.sunnyweather.android.utils.showToast
import com.sunnyweather.android.weather.WeatherActivity
import kotlinx.coroutines.launch

class PlaceFragment : Fragment() {

    val viewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }

    private lateinit var _binding: FragmentPlaceBinding
    private val binding get() = _binding
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layout = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = layout
        adapter = PlaceAdapter(this, viewModel.placeList)
        binding.recyclerView.adapter = adapter

        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val savedPlace = viewModel.getSavedPlace()
            val locationData = LocationData(savedPlace.location.lng, savedPlace.location.lat, savedPlace.name)
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_data", locationData)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        binding.searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch {
            viewModel.placeFlow.collect { result ->
                val places = result.getOrNull()
                if (places != null) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.bgImageView.visibility = View.GONE
                    viewModel.placeList.clear()
                    viewModel.placeList.addAll(places)
                    adapter.notifyDataSetChanged()
                } else {
                    activity?.showToast("未能查询到任何地点")
                    result.exceptionOrNull()?.printStackTrace()
                }
            }
        }
    }
}