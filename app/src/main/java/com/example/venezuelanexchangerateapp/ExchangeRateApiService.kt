package com.example.venezuelanexchangerateapp

import retrofit2.http.GET

import retrofit2.http.Path

interface ExchangeRateApiService {
    @GET("api/v1/rates/venezuela") // Hypothetical endpoint
    suspend fun getLatestRates(): ExchangeRateResponse

    @GET("api/v1/rates/venezuela/historical/{date}") // date format YYYY-MM-DD
    suspend fun getHistoricalRates(@Path("date") date: String): ExchangeRateResponse
}
