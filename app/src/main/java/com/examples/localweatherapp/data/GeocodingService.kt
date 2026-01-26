package com.examples.localweatherapp.data

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("v1/search")
    suspend fun searchLocation(
        @Query("name") name: String,
        @Query("count") count: Int = 1
    ): GeocodingResponse
}
