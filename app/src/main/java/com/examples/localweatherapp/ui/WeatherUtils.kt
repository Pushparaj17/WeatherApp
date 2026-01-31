package com.examples.localweatherapp.ui

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
