package com.example.weatherforecastapp.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherforecastapp.BuildConfig
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.adapters.VpAdapter
import com.example.weatherforecastapp.adapters.WeatherModel
import com.example.weatherforecastapp.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONObject

class MainFragment : Fragment() {
    private lateinit var pLauncher : ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val fList = listOf (
        Hours.newInstance(),
        Days.newInstance(),
    )
    private val tList = listOf("hours", "days")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
    }

    private fun init() = with(binding) {
        val adapter = VpAdapter(activity as AppCompatActivity, fList as List<Fragment>)
        vp.adapter = adapter
        requestWeatherData("London")
        TabLayoutMediator(tlMenu, vp) {
            tab, pos -> tab.text = tList[pos].toString()
        }.attach()
    }
    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun requestWeatherData(city : String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=${Companion.API}&q=$city&days=3&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                result -> Log.d("LogAPI", "Success: $result")
            },
            {
                error -> Log.d("LogAPI", "Error: $error")
            }
        )
        queue.add(request)
    }

    private fun parseWeatherData(result : String) {
        val mainObject = JSONObject(result)
        var list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])

    }

    fun parseCurrentData(mainObject : JSONObject, weatherModel : WeatherModel) {
        val currentObj = mainObject.getJSONObject("location")
        val item = WeatherModel(
            mainObject.getString("location"),
            currentObj.getString("last_updated"),
            currentObj.getJSONObject("condition").getString("text"),
            currentObj.getString("temp_c"),
            weatherModel.maxTemp,
            weatherModel.minTemp,
            currentObj.getJSONObject("condition").getString("icon"),
            weatherModel.hours
        )
    }

    fun parseDays(mainObject: JSONObject) : List<WeatherModel> {
        var list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val dayObj = day.getJSONObject("day")
            val item = WeatherModel(
                name,
                day.getString("date"),
                dayObj.getJSONObject("condition").getString("text"),
                dayObj.getString("avgtemp_c"),
                dayObj.getString("mintemp_c"),
                dayObj.getString("maxtemp_c"),
                dayObj.getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        return list
    }
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
        const private val API = BuildConfig.API_KEY
    }
}