package com.example.venezuelanexchangerateapp

import android.os.Bundle
import android.util.Log
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoricalRatesActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var buttonFetchRates: Button
    private lateinit var textViewUSDRate: TextView
    private lateinit var textViewEURRate: TextView
    private lateinit var progressBarHistorical: ProgressBar
    private var selectedDateString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historical_rates)

        calendarView = findViewById(R.id.calendarViewHistorical)
        buttonFetchRates = findViewById(R.id.buttonFetchHistoricalRates)
        textViewUSDRate = findViewById(R.id.textViewHistoricalUSDRate)
        textViewEURRate = findViewById(R.id.textViewHistoricalEURRate)
        progressBarHistorical = findViewById(R.id.progressBarHistorical)

        // Set CalendarView to not allow future dates
        calendarView.maxDate = System.currentTimeMillis()

        // Get today's date as default selected
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDateString = sdf.format(Calendar.getInstance().time)


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Month is 0-based, so add 1
            selectedDateString = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
            // Clear previous rates on new date selection
            textViewUSDRate.text = getString(R.string.usd_rate_historical_placeholder)
            textViewEURRate.text = getString(R.string.eur_rate_historical_placeholder)
        }

        buttonFetchRates.setOnClickListener {
            if (selectedDateString.isNotEmpty()) {
                fetchHistoricalRates(selectedDateString)
            } else {
                Toast.makeText(this, "Please select a date first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchHistoricalRates(date: String) {
        progressBarHistorical.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getHistoricalRates(date)
                // Using existing string formats from MainActivity for consistency
                textViewUSDRate.text = getString(R.string.usd_rate_format, response.rates.usd.official)
                textViewEURRate.text = getString(R.string.eur_rate_format, response.rates.eur.official)
            } catch (e: Exception) {
                Log.e("HISTORICAL_API_ERROR", "Error fetching historical rates for $date", e)
                textViewUSDRate.text = getString(R.string.usd_rate_historical_error)
                textViewEURRate.text = getString(R.string.eur_rate_historical_error)
                Toast.makeText(this@HistoricalRatesActivity, "Error fetching rates for $date. Data might not be available.", Toast.LENGTH_LONG).show()
            } finally {
                progressBarHistorical.visibility = View.GONE
            }
        }
    }
}
