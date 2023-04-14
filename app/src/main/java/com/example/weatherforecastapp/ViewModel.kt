package com.example.weatherforecastapp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class ViewModel : ViewModel() {
    val lifeDataCurrent = MutableLiveData<String>()
    val lifeDataList = MutableLiveData<List<String>>()
}