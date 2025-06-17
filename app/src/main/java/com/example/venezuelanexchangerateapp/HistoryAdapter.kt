package com.example.venezuelanexchangerateapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val historyList: List<ConversionRecord>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val detailsTextView: TextView = view.findViewById(R.id.textViewConversionDetails)
        val rateTextView: TextView = view.findViewById(R.id.textViewConversionRate)
        val timestampTextView: TextView = view.findViewById(R.id.textViewConversionTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = historyList[position]
        holder.detailsTextView.text = String.format(
            "%.2f %s → %.2f %s",
            record.fromAmount, record.fromCurrency, record.toAmount, record.toCurrency
        )
        holder.rateTextView.text = String.format(
            "Rate: %.2f (%s)",
            record.rateUsed, record.rateType
        )
        holder.timestampTextView.text = record.getFormattedTimestamp()
    }

    override fun getItemCount() = historyList.size
}
