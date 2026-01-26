package com.examples.localweatherapp.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examples.localweatherapp.data.WeatherResponse
import com.examples.localweatherapp.domain.GetWeatherUseCase
import com.examples.localweatherapp.domain.SearchLocationUseCase
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val searchLocationUseCase: SearchLocationUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        setupSearchDebounce()
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(600)
                .filter { it.isNotBlank() && it.length > 2 }
                .collect { query ->
                    searchAndFetchWeather(query)
                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun searchAndFetchWeather(query: String) {
        viewModelScope.launch {
            try {
                val geocodingResponse = searchLocationUseCase(query)
                val result = geocodingResponse.results?.firstOrNull()
                if (result != null) {
                    fetchWeatherForLocation(result.latitude, result.longitude, result.name, result.postcodes?.firstOrNull() ?: "N/A")
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).await()

                if (location != null) {
                    val addressInfo = withContext(Dispatchers.IO) {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val address = addresses[0]
                                Pair(address.locality ?: "Unknown City", address.postalCode ?: "No ZIP")
                            } else {
                                Pair("Unknown Location", "N/A")
                            }
                        } catch (e: Exception) {
                            Pair("Location Error", "N/A")
                        }
                    }
                    fetchWeatherForLocation(location.latitude, location.longitude, addressInfo.first, addressInfo.second)
                } else {
                    _uiState.value = WeatherUiState.Error("Could not fetch location")
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun fetchWeatherForLocation(lat: Double, lon: Double, cityName: String, zipCode: String) {
        try {
            val weatherResponse = getWeatherUseCase(lat, lon)
            val currentHour = LocalDateTime.now().hour

            _uiState.value = WeatherUiState.Success(
                weatherData = weatherResponse,
                cityName = cityName,
                zipCode = zipCode,
                selectedDateIndex = 0,
                selectedHourOfDay = currentHour
            )
        } catch (e: Exception) {
            _uiState.value = WeatherUiState.Error(e.message ?: "Unknown error")
        }
    }

    fun selectDate(index: Int) {
        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(selectedDateIndex = index)
        }
    }

    fun selectHour(hourOfDay: Int) {
        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(selectedHourOfDay = hourOfDay)
        }
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val weatherData: WeatherResponse,
        val cityName: String,
        val zipCode: String,
        val selectedDateIndex: Int,
        val selectedHourOfDay: Int
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
