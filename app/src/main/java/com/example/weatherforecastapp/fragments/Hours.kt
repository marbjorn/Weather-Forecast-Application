package com.example.weatherforecastapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.adapters.WeatherAdapter
import com.example.weatherforecastapp.adapters.WeatherModel
import com.example.weatherforecastapp.databinding.FragmentHoursBinding

class Hours : Fragment() {
    private lateinit var binding : FragmentHoursBinding
    private lateinit var adapter : WeatherAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter()
        rcView.adapter= adapter
        val list = listOf(WeatherModel(
            time = "18:00",
            condition = "Sunny",
            currentTemp = "25C"),
            WeatherModel(
                time = "19:00",
                condition = "Cloudy",
                currentTemp = "22C"),
            WeatherModel(
                time = "20:00",
                condition = "Rainy",
                currentTemp = "19C")
        )
        adapter.submitList(list)
    }
    companion object {
        @JvmStatic
        fun newInstance() = Hours()
    }
}