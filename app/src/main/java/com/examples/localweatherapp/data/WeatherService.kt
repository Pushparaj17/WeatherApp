package com.examples.localweatherapp.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("v1/forecast")
    suspend fun getWeatherData(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("hourly") hourly: String = "temperature_2m,weathercode",
        @Query("daily") daily: String = "weathercode,temperature_2m_max",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
