package com.example.weatherforecastapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecastapp.MainViewModel
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.adapters.WeatherAdapter
import com.example.weatherforecastapp.adapters.WeatherModel
import com.example.weatherforecastapp.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject

class Hours : Fragment() {
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter

    private val model : MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.lifeDataCurrent.observe(viewLifecycleOwner) {
            adapter.submitList(getHoursList(it))
        }
    }

    private fun initRcView() = with(binding){
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter(null)
        rcView.adapter = adapter
    }

    private fun getHoursList(item : WeatherModel) : List<WeatherModel> {
        val objArray = JSONArray(item.hours)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until objArray.length()) {
            val hourObj = objArray[i] as JSONObject
            val hour = WeatherModel(
                city = item.city,
                time = hourObj.getString("time"),
                condition = hourObj.getJSONObject("condition").getString("text"),
                currentTemp = hourObj.getString("temp_c"),
                imageUrl = hourObj.getJSONObject("condition").getString("icon")
            )
            list.add(hour)
        }
        return list
    }
    companion object {
        @JvmStatic
        fun newInstance() = Hours()
    }
}