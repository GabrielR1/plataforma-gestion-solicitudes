package com.example.venezuelanexchangerateapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private var conversionHistoryList: MutableList<ConversionRecord> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerViewHistory = findViewById(R.id.recyclerViewHistory)
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)

        loadHistory()
        historyAdapter = HistoryAdapter(conversionHistoryList)
        recyclerViewHistory.adapter = historyAdapter
    }

    private fun loadHistory() {
        val prefs = getSharedPreferences("ConversionHistoryPrefs", Context.MODE_PRIVATE) // Same as PREFS_NAME in MainActivity
        val gson = Gson()
        val historyJson = prefs.getString("history", null) // Same as HISTORY_KEY in MainActivity
        val type = object : TypeToken<MutableList<ConversionRecord>>() {}.type
        conversionHistoryList = gson.fromJson(historyJson, type) ?: mutableListOf()
        // Ensure the list is in descending order of timestamp for display (most recent first)
        // SharedPreferences already saves it in the correct order (newest at index 0)
    }
}
