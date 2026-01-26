package com.examples.localweatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.examples.localweatherapp.presentation.WeatherUiState
import com.examples.localweatherapp.presentation.WeatherViewModel
import com.examples.localweatherapp.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            locationPermissionGranted = isGranted
            if (isGranted) viewModel.fetchWeather()
        }
    )

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.fetchWeather()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            )
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChange(it) }
        )

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (!locationPermissionGranted && searchQuery.isBlank()) {
                Text(
                    "Location permission is required or search for a city.",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                when (val state = uiState) {
                    is WeatherUiState.Loading -> CircularProgressIndicator(color = BlueSecondary)
                    is WeatherUiState.Error -> Text(
                        text = "Error: ${state.message}",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    is WeatherUiState.Success -> WeatherContent(state, viewModel)
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Search City or ZIP Code", color = TextSecondary) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlueSecondary,
            unfocusedBorderColor = CardBackground,
            focusedContainerColor = CardBackground,
            unfocusedContainerColor = CardBackground,
            cursorColor = BlueSecondary
        )
    )
}

@Composable
fun WeatherContent(state: WeatherUiState.Success, viewModel: WeatherViewModel) {
    val absoluteHourIndex = (state.selectedDateIndex * 24) + state.selectedHourOfDay
    
    val selectedTemp = state.weatherData.hourly.temperatures.getOrNull(absoluteHourIndex) ?: 0.0
    val selectedTime = state.weatherData.hourly.time.getOrNull(absoluteHourIndex) ?: ""
    val weatherCode = state.weatherData.hourly.weatherCodes.getOrNull(absoluteHourIndex) ?: 0
    
    val hourListState = rememberLazyListState()
    val dateListState = rememberLazyListState()

    LaunchedEffect(state.weatherData, state.cityName) {
        hourListState.animateScrollToItem(state.selectedHourOfDay)
        dateListState.animateScrollToItem(state.selectedDateIndex)
    }

    val weatherImageUrl = remember(weatherCode) {
        getWeatherImageUrl(weatherCode)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weather at ${selectedTime.substringAfter("T")}",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = "${selectedTemp}°C",
            style = MaterialTheme.typography.displayLarge
        )

        AsyncImage(
            model = weatherImageUrl,
            contentDescription = "Weather Image",
            modifier = Modifier
                .size(180.dp)
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = state.cityName,
            style = MaterialTheme.typography.headlineSmall
        )
        
        Text(
            text = "ZIP: ${state.zipCode}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Hourly Forecast",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 12.dp)
        )

        val startHour = state.selectedDateIndex * 24
        val dayHours = state.weatherData.hourly.time.subList(startHour, startHour + 24)

        LazyRow(
            state = hourListState,
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(dayHours) { index, time ->
                val temp = state.weatherData.hourly.temperatures[startHour + index]
                val isSelected = state.selectedHourOfDay == index
                
                ForecastItem(
                    label = time.substringAfter("T"),
                    value = "${temp.toInt()}°",
                    isSelected = isSelected,
                    onClick = { viewModel.selectHour(index) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Weekly Forecast",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 12.dp)
        )

        LazyRow(
            state = dateListState,
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(state.weatherData.daily.time) { index, date ->
                val maxTemp = state.weatherData.daily.maxTemperatures[index]
                val isSelected = state.selectedDateIndex == index
                
                ForecastItem(
                    label = date.substring(5),
                    value = "${maxTemp.toInt()}°",
                    isSelected = isSelected,
                    onClick = { viewModel.selectDate(index) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ForecastItem(
    label: String,
    value: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(85.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BlueSecondary.copy(alpha = 0.8f) else CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.Black else TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) Color.Black else TextPrimary
            )
        }
    }
}

fun getWeatherImageUrl(code: Int): String {
    return when (code) {
        0 -> "https://openweathermap.org/img/wn/01d@4x.png"
        1, 2, 3 -> "https://openweathermap.org/img/wn/02d@4x.png"
        45, 48 -> "https://openweathermap.org/img/wn/50d@4x.png"
        51, 53, 55 -> "https://openweathermap.org/img/wn/09d@4x.png"
        61, 63, 65 -> "https://openweathermap.org/img/wn/10d@4x.png"
        71, 73, 75 -> "https://openweathermap.org/img/wn/13d@4x.png"
        95, 96, 99 -> "https://openweathermap.org/img/wn/11d@4x.png"
        else -> "https://openweathermap.org/img/wn/01d@4x.png"
    }
}
