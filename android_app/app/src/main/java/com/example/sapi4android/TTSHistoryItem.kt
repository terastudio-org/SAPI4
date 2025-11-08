package com.example.sapi4android

import java.util.Date

data class TTSHistoryItem(
    val id: String = System.currentTimeMillis().toString(),
    val text: String,
    val voice: String?,
    val pitch: Float,
    val speed: Float,
    val timestamp: Date = Date()
)