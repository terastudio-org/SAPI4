package com.example.sapi4android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private var historyItems: MutableList<TTSHistoryItem>,
    private val onItemClick: (TTSHistoryItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyItems[position])
    }

    override fun getItemCount(): Int = historyItems.size

    fun updateHistory(newHistoryItems: List<TTSHistoryItem>) {
        historyItems.clear()
        historyItems.addAll(newHistoryItems)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewHistoryText: TextView = itemView.findViewById(R.id.textViewHistoryText)
        private val textViewVoice: TextView = itemView.findViewById(R.id.textViewVoice)
        private val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)

        fun bind(historyItem: TTSHistoryItem) {
            textViewHistoryText.text = historyItem.text
            textViewVoice.text = "Voice: ${historyItem.voice ?: "Default"} | Pitch: ${String.format("%.2f", historyItem.pitch)} | Speed: ${String.format("%.2f", historyItem.speed)}"
            
            val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            textViewTimestamp.text = dateFormat.format(historyItem.timestamp)

            itemView.setOnClickListener {
                onItemClick(historyItem)
            }
        }
    }
}