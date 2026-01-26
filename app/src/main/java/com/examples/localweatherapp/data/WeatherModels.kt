package com.examples.localweatherapp.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather,
    val hourly: HourlyData,
    val daily: DailyData
)

data class CurrentWeather(
    val temperature: Double,
    @SerializedName("weathercode")
    val weatherCode: Int,
    val time: String
)

data class HourlyData(
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temperatures: List<Double>,
    @SerializedName("weathercode")
    val weatherCodes: List<Int>
)

data class DailyData(
    val time: List<String>,
    @SerializedName("weathercode")
    val weatherCodes: List<Int>,
    @SerializedName("temperature_2m_max")
    val maxTemperatures: List<Double>
)
