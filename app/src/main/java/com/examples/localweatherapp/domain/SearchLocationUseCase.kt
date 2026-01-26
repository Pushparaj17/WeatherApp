package com.examples.localweatherapp.domain

import com.examples.localweatherapp.data.GeocodingResponse
import javax.inject.Inject

class SearchLocationUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(query: String): GeocodingResponse {
        return repository.searchLocation(query)
    }
}
