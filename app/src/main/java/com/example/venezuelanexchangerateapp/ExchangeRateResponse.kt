package com.example.venezuelanexchangerateapp

import com.google.gson.annotations.SerializedName

data class ExchangeRateResponse(
    val date: String,
    val rates: Rates
)

data class Rates(
    @SerializedName("USD") val usd: CurrencyRate,
    @SerializedName("EUR") val eur: CurrencyRate
)

data class CurrencyRate(
    val official: Double
)
