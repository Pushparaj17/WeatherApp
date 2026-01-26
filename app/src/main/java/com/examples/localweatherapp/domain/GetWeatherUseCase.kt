package com.examples.localweatherapp.domain

import com.examples.localweatherapp.data.WeatherResponse
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): WeatherResponse {
        return repository.getWeatherData(lat, lon)
    }
}
