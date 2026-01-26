package com.examples.localweatherapp.domain

import com.examples.localweatherapp.data.GeocodingResponse
import com.examples.localweatherapp.data.WeatherResponse

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, lon: Double): WeatherResponse
    suspend fun searchLocation(query: String): GeocodingResponse
}
