package com.example.venezuelanexchangerateapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private const val PREFS_NAME = "ConversionHistoryPrefs"
    private const val HISTORY_KEY = "history"
    private const val MAX_HISTORY_SIZE = 50 // Max records

    // Existing UI Elements
    private lateinit var textViewUSDRate: TextView
    private lateinit var textViewEURRate: TextView
    private lateinit var editTextAmountToConvert: EditText
    private lateinit var editTextConvertedAmount: EditText
    private lateinit var spinnerFromCurrency: Spinner
    private lateinit var spinnerToCurrency: Spinner
    private lateinit var buttonConvert: Button
    private lateinit var imageViewCopyAmountToConvert: ImageView
    private lateinit var imageViewCopyConvertedAmount: ImageView
    private lateinit var buttonViewHistory: Button
    private lateinit var buttonViewCalendar: Button

    // New UI Elements
    private lateinit var radioGroupRateSelection: RadioGroup
    private lateinit var radioButtonOfficialUSD: RadioButton
    private lateinit var radioButtonOfficialEUR: RadioButton
    private lateinit var radioButtonCustomRate: RadioButton
    private lateinit var editTextCustomRate: EditText
    private lateinit var textViewSelectedRateDisplay: TextView

    // Rate variables
    private var officialUSDRate: Double = 0.0
    private var officialEURRate: Double = 0.0
    private var currentRate: Double = 0.0
    private var currentRateCurrency: String = "USD" // Default to USD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Existing UI Elements
        textViewUSDRate = findViewById(R.id.textViewUSDRate)
        textViewEURRate = findViewById(R.id.textViewEURRate)
        editTextAmountToConvert = findViewById(R.id.editTextAmountToConvert)
        editTextConvertedAmount = findViewById(R.id.editTextConvertedAmount)
        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrency)
        spinnerToCurrency = findViewById(R.id.spinnerToCurrency)
        buttonConvert = findViewById(R.id.buttonConvert)
        imageViewCopyAmountToConvert = findViewById(R.id.imageViewCopyAmountToConvert)
        imageViewCopyConvertedAmount = findViewById(R.id.imageViewCopyConvertedAmount)
        buttonViewHistory = findViewById(R.id.buttonViewHistory)
        buttonViewCalendar = findViewById(R.id.buttonViewCalendar)

        // Initialize New UI Elements
        radioGroupRateSelection = findViewById(R.id.radioGroupRateSelection)
        radioButtonOfficialUSD = findViewById(R.id.radioButtonOfficialUSD)
        radioButtonOfficialEUR = findViewById(R.id.radioButtonOfficialEUR)
        radioButtonCustomRate = findViewById(R.id.radioButtonCustomRate)
        editTextCustomRate = findViewById(R.id.editTextCustomRate)
        textViewSelectedRateDisplay = findViewById(R.id.textViewSelectedRateDisplay)

        // Populate Spinners
        val currencies = resources.getStringArray(R.array.currency_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFromCurrency.adapter = adapter
        spinnerToCurrency.adapter = adapter

        // Set Initial State
        radioButtonOfficialUSD.isChecked = true
        editTextCustomRate.visibility = View.GONE
        currentRate = officialUSDRate // Initialize with a default even if 0.0
        currentRateCurrency = "USD"
        updateSelectedRateDisplay()

        // Set up Listeners
        buttonConvert.setOnClickListener {
            performConversion()
        }

        imageViewCopyAmountToConvert.setOnClickListener {
            copyToClipboard(editTextAmountToConvert.text.toString(), "Amount to Convert")
        }

        imageViewCopyConvertedAmount.setOnClickListener {
            copyToClipboard(editTextConvertedAmount.text.toString(), "Converted Amount")
        }

        buttonViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        buttonViewCalendar.setOnClickListener {
            val intent = Intent(this, HistoricalRatesActivity::class.java)
            startActivity(intent)
        }

        radioGroupRateSelection.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonOfficialUSD -> {
                    currentRate = officialUSDRate
                    currentRateCurrency = "USD"
                    editTextCustomRate.visibility = View.GONE
                }
                R.id.radioButtonOfficialEUR -> {
                    currentRate = officialEURRate
                    currentRateCurrency = "EUR"
                    editTextCustomRate.visibility = View.GONE
                }
                R.id.radioButtonCustomRate -> {
                    editTextCustomRate.visibility = View.VISIBLE
                    // Attempt to use custom rate, fallback to official USD if empty/invalid
                    val customRateText = editTextCustomRate.text.toString()
                    currentRate = customRateText.toDoubleOrNull() ?: officialUSDRate
                    currentRateCurrency = "Custom" // Or "USD" if custom is always USD based
                }
            }
            updateSelectedRateDisplay()
        }

        editTextCustomRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (radioButtonCustomRate.isChecked) {
                    val customRateText = s.toString()
                    currentRate = customRateText.toDoubleOrNull() ?: officialUSDRate // Fallback
                    // currentRateCurrency = "Custom" // Or "USD"
                    updateSelectedRateDisplay()
                }
            }
        })

        fetchRates()
    }

    private fun updateSelectedRateDisplay() {
        // Ensure currentRateCurrency is not "Custom" when official rates are used for display text
        val displayCurrency = when {
            radioButtonOfficialUSD.isChecked -> "USD"
            radioButtonOfficialEUR.isChecked -> "EUR"
            else -> currentRateCurrency // For "Custom"
        }
        textViewSelectedRateDisplay.text = getString(R.string.selected_rate_display_format, currentRate, displayCurrency)
    }


    private fun fetchRates() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getLatestRates()
                officialUSDRate = response.rates.usd.official
                officialEURRate = response.rates.eur.official
                textViewUSDRate.text = getString(R.string.usd_rate_format, officialUSDRate)
                textViewEURRate.text = getString(R.string.eur_rate_format, officialEURRate)

                // Update currentRate and its display if the relevant radio button is still checked
                if (radioButtonOfficialUSD.isChecked) {
                    currentRate = officialUSDRate
                    currentRateCurrency = "USD"
                    updateSelectedRateDisplay()
                } else if (radioButtonOfficialEUR.isChecked) {
                    currentRate = officialEURRate
                    currentRateCurrency = "EUR"
                    updateSelectedRateDisplay()
                }
                // If custom is checked, its TextWatcher will handle updates.
            } catch (e: Exception) {
                Log.e("API_FETCH_ERROR", "Error fetching rates", e)
                Toast.makeText(this@MainActivity, "Error fetching rates", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performConversion() {
        val amountString = editTextAmountToConvert.text.toString()
        if (amountString.isEmpty()) {
            Toast.makeText(this, "Please enter an amount to convert", Toast.LENGTH_SHORT).show()
            return
        }

        val amountToConvert = amountString.toDoubleOrNull()
        if (amountToConvert == null || amountToConvert <= 0) {
            Toast.makeText(this, "Invalid amount entered", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentRate <= 0) {
            Toast.makeText(this, "Current rate is not valid for conversion", Toast.LENGTH_SHORT).show()
            return
        }

        val fromCurrencySpinner = spinnerFromCurrency.selectedItem.toString()
        val toCurrencySpinner = spinnerToCurrency.selectedItem.toString()
        var convertedAmount: Double? = null

        // currentRateCurrency is "USD", "EUR", or "Custom" (which we'll treat as USD-based for now)
        val effectiveRateCurrency = if (currentRateCurrency == "Custom") "USD" else currentRateCurrency

        if (fromCurrencySpinner == effectiveRateCurrency && toCurrencySpinner == "VEF") {
            convertedAmount = amountToConvert * currentRate
        } else if (fromCurrencySpinner == "VEF" && toCurrencySpinner == effectiveRateCurrency) {
            convertedAmount = amountToConvert / currentRate
        } else if (fromCurrencySpinner == toCurrencySpinner) {
            convertedAmount = amountToConvert
        } else {
            // This handles cases like USD -> EUR, or trying to use EUR rate for USD conversion etc.
            Toast.makeText(this, "Rate mismatch or conversion not supported for selected currencies and rate type.", Toast.LENGTH_LONG).show()
            return
        }

        if (convertedAmount != null) {
            val df = DecimalFormat("#.##")
            editTextConvertedAmount.setText(df.format(convertedAmount))

            val recordRateType = when {
                radioButtonOfficialUSD.isChecked -> "Official USD"
                radioButtonOfficialEUR.isChecked -> "Official EUR"
                radioButtonCustomRate.isChecked -> "Custom (${editTextCustomRate.text})"
                else -> "Unknown"
            }
            val newRecord = ConversionRecord(
                fromCurrency = fromCurrencySpinner,
                fromAmount = amountToConvert,
                toCurrency = toCurrencySpinner,
                toAmount = convertedAmount,
                rateUsed = currentRate,
                rateType = recordRateType,
                timestamp = System.currentTimeMillis()
            )
            saveConversionRecord(newRecord)
        }
    }

    private fun saveConversionRecord(record: ConversionRecord) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val historyJson = prefs.getString(HISTORY_KEY, null)
        val type = object : TypeToken<MutableList<ConversionRecord>>() {}.type
        val history: MutableList<ConversionRecord> = gson.fromJson(historyJson, type) ?: mutableListOf()

        history.add(0, record) // Add to the beginning (most recent)
        while (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1) // Remove oldest if limit exceeded
        }

        val editor = prefs.edit()
        editor.putString(HISTORY_KEY, gson.toJson(history))
        editor.apply()
    }

    private fun copyToClipboard(textToCopy: String, label: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$label copied!", Toast.LENGTH_SHORT).show()
    }
}
