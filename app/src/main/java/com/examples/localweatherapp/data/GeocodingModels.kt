package com.examples.localweatherapp.data

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    val results: List<GeocodingResult>?
)

data class GeocodingResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    @SerializedName("admin1")
    val state: String?,
    val postcodes: List<String>?
)
