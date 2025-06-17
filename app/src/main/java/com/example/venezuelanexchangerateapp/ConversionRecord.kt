package com.example.venezuelanexchangerateapp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ConversionRecord(
    val fromCurrency: String,
    val fromAmount: Double,
    val toCurrency: String,
    val toAmount: Double,
    val rateUsed: Double,
    val rateType: String, // "Official USD", "Official EUR", "Custom"
    val timestamp: Long
) {
    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
