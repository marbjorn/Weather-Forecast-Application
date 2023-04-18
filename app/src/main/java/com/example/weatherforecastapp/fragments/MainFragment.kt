package com.example.weatherforecastapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherforecastapp.BuildConfig
import com.example.weatherforecastapp.MainViewModel
import com.example.weatherforecastapp.Parser
import com.example.weatherforecastapp.adapters.VpAdapter
import com.example.weatherforecastapp.adapters.WeatherModel
import com.example.weatherforecastapp.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject
import kotlin.math.roundToInt

class MainFragment : Fragment() {
    private lateinit var fLocationClient : FusedLocationProviderClient
    private lateinit var pLauncher : ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val fList = listOf (
        Hours.newInstance(),
        Days.newInstance(),
    )
    private val tList = listOf("hours", "days")

    private val model : MainViewModel by activityViewModels()
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
        updateCurrentCard()
        getLocation()
    }


    fun requestWeatherData(city : String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=${MainFragment.API}&q=$city&days=3&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                    result -> parseWeatherData(result)
            },
            {
                    error -> Log.d("LogAPI", "Error: $error")
            }
        )
        queue.add(request)
    }
    private fun init() = with(binding) {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = VpAdapter(activity as AppCompatActivity, fList)
        vp.adapter = adapter
        TabLayoutMediator(tlMenu, vp) {
            tab, pos -> tab.text = tList[pos]
        }.attach()
        ibRefresh.setOnClickListener{
            tlMenu.selectTab(tlMenu.getTabAt(0))
            getLocation()
        }
    }

    private fun getLocation() {
        val cl = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient
            .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cl.token)
            .addOnCompleteListener{
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
            }
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

    private fun parseWeatherData(result : String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])

    }

    fun parseCurrentData(mainObject : JSONObject, weatherModel : WeatherModel) {
        val currentObj = mainObject.getJSONObject("current")
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            currentObj.getString("last_updated"),
            currentObj.getJSONObject("condition").getString("text"),
            currentObj.getString("temp_c"),
            weatherModel.maxTemp,
            weatherModel.minTemp,
            currentObj.getJSONObject("condition").getString("icon"),
            weatherModel.hours
        )
        Log.d("LogAPI", "data is parsed")
        model.lifeDataCurrent.value = item
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
                "",
                dayObj.getString("maxtemp_c").toFloat().roundToInt().toString(),
                dayObj.getString("mintemp_c").toFloat().roundToInt().toString(),
                dayObj.getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.lifeDataList.value = list
        return list
    }
    private fun updateCurrentCard() = with(binding){
        model.lifeDataCurrent.observe(viewLifecycleOwner){
            val maxMinTemp = "${it.maxTemp}°C/${it.minTemp}°C"
            tvCity.text = it.city
            tvData.text = it.time
            tvCurrentTemp.text = if(it.currentTemp.isEmpty()) maxMinTemp else "${it.currentTemp}°C"
            tvMaxMinTemp.text = if(it.currentTemp.isEmpty()) "" else maxMinTemp
            tvWeatherCondition.text = it.condition
            Picasso.get().load("https:" + it.imageUrl).into(imWeather)
            Log.d("LogAPI", "updated")
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
        const private val API = BuildConfig.API_KEY
    }
}