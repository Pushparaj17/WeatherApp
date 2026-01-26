package com.examples.localweatherapp.data

import com.examples.localweatherapp.domain.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val geocodingService: GeocodingService
) : WeatherRepository {
    override suspend fun getWeatherData(lat: Double, lon: Double): WeatherResponse {
        return weatherService.getWeatherData(lat, lon)
    }

    override suspend fun searchLocation(query: String): GeocodingResponse {
        return geocodingService.searchLocation(query)
    }
}
