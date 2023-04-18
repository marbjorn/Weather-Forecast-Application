package com.example.weatherforecastapp.fragments

data class DayItem(
    val city : String,
    val time : String,
    val weatherCondition : String,
    val imageUrl : String,
    val currentTemp : String,
    val maxTemp : String,
    val minTemp : String,
    val hours : String
)
