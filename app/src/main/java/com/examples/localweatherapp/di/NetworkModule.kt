package com.examples.localweatherapp.di

import com.examples.localweatherapp.data.GeocodingService
import com.examples.localweatherapp.data.WeatherRepositoryImpl
import com.examples.localweatherapp.data.WeatherService
import com.examples.localweatherapp.domain.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideWeatherService(): WeatherService {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingService(): GeocodingService {
        return Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherService: WeatherService,
        geocodingService: GeocodingService
    ): WeatherRepository {
        return WeatherRepositoryImpl(weatherService, geocodingService)
    }
}
