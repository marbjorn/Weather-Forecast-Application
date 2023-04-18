package com.example.weatherforecastapp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherforecastapp.adapters.WeatherModel

class MainViewModel : ViewModel() {
    val lifeDataCurrent = MutableLiveData<WeatherModel>()
    val lifeDataList = MutableLiveData<List<WeatherModel>>()
}