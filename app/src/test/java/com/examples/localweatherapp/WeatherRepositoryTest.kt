package com.examples.localweatherapp

import com.examples.localweatherapp.data.*
import com.examples.localweatherapp.domain.WeatherRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private lateinit var repository: WeatherRepository
    private val weatherService = mockk<WeatherService>()
    private val geocodingService = mockk<GeocodingService>()

    @Before
    fun setup() {
        repository = WeatherRepositoryImpl(weatherService, geocodingService)
    }

    @Test
    fun `getWeatherData should return weather response from service`() = runBlocking {
        // Arrange
        val expectedResponse = mockk<WeatherResponse>()
        coEvery { weatherService.getWeatherData(any(), any()) } returns expectedResponse

        // Act
        val result = repository.getWeatherData(1.0, 1.0)

        // Assert
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `searchLocation should return geocoding response from service`() = runBlocking {
        // Arrange
        val expectedResponse = mockk<GeocodingResponse>()
        coEvery { geocodingService.searchLocation(any()) } returns expectedResponse

        // Act
        val result = repository.searchLocation("London")

        // Assert
        assertEquals(expectedResponse, result)
    }
}
