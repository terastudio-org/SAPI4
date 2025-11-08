package com.example.sapi4android

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class TTSManager(private val context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var onInitListener: ((Boolean) -> Unit)? = null
    private var onUtteranceCompleteListener: (() -> Unit)? = null
    
    var isInitialized = false
        private set
    
    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    private val gson = Gson()
    
    fun initialize(onComplete: (Boolean) -> Unit) {
        onInitListener = onComplete
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                onInitListener?.invoke(true)
                
                // Set default voice if available
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val preferredVoice = getPreferredVoice()
                    preferredVoice?.let {
                        textToSpeech?.voice = it
                    }
                }
            } else {
                Log.e("TTSManager", "Failed to initialize TTS")
                onInitListener?.invoke(false)
            }
        }
        
        // Set up utterance completion listener
        textToSpeech?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            
            override fun onDone(utteranceId: String?) {
                onUtteranceCompleteListener?.invoke()
            }
            
            override fun onError(utteranceId: String?) {
                Log.e("TTSManager", "Error in utterance: $utteranceId")
            }
            
            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e("TTSManager", "Error in utterance: $utteranceId, code: $errorCode")
            }
        })
    }
    
    private fun getPreferredVoice(): Voice? {
        return textToSpeech?.getVoices()?.find { 
            it.locale == Locale.getDefault() && it.isNetworkConnectionRequired == false
        } ?: textToSpeech?.getVoices()?.firstOrNull()
    }
    
    fun speak(text: String, pitch: Float = 1.0f, speed: Float = 1.0f, voiceName: String? = null) {
        if (!isInitialized) return
        
        // Set pitch and speed
        textToSpeech?.setPitch(pitch)
        textToSpeech?.setSpeechRate(speed)
        
        // Set voice if specified
        if (!voiceName.isNullOrEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val selectedVoice = textToSpeech?.getVoices()?.find { it.name == voiceName }
            selectedVoice?.let {
                textToSpeech?.voice = it
            }
        }
        
        // Speak the text
        val utteranceId = System.currentTimeMillis().toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } else {
            @Suppress("DEPRECATION")
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
        
        // Add to history
        addToHistory(TTSHistoryItem(text = text, voice = voiceName, pitch = pitch, speed = speed))
    }
    
    fun stop() {
        if (isInitialized) {
            textToSpeech?.stop()
        }
    }
    
    fun setOnUtteranceCompleteListener(listener: () -> Unit) {
        onUtteranceCompleteListener = listener
    }
    
    fun getAvailableVoices(): List<String> {
        if (!isInitialized) return emptyList()
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech?.getVoices()?.map { it.name } ?: emptyList()
        } else {
            // For older versions, return default voices
            listOf("Default")
        }
    }
    
    fun shutdown() {
        textToSpeech?.shutdown()
        isInitialized = false
    }
    
    fun getCurrentVoice(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isInitialized) {
            textToSpeech?.voice?.name
        } else null
    }
    
    fun setVoice(voiceName: String) {
        if (!isInitialized || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
        
        val selectedVoice = textToSpeech?.getVoices()?.find { it.name == voiceName }
        selectedVoice?.let {
            textToSpeech?.voice = it
        }
    }
    
    fun setPitch(pitch: Float) {
        if (isInitialized) {
            textToSpeech?.setPitch(pitch)
        }
    }
    
    fun setSpeed(speed: Float) {
        if (isInitialized) {
            textToSpeech?.setSpeechRate(speed)
        }
    }
    
    // History management
    private fun addToHistory(item: TTSHistoryItem) {
        val history = getHistoryItems()
        history.add(0, item) // Add to the beginning
        
        // Limit history to 50 items
        if (history.size > 50) {
            history.subList(50, history.size).clear()
        }
        
        saveHistory(history)
    }
    
    fun getHistoryItems(): MutableList<TTSHistoryItem> {
        val historyJson = preferences.getString("tts_history", "[]") ?: "[]"
        val type = object : TypeToken<MutableList<TTSHistoryItem>>() {}.type
        return gson.fromJson(historyJson, type) ?: mutableListOf()
    }
    
    private fun saveHistory(history: List<TTSHistoryItem>) {
        val historyJson = gson.toJson(history)
        preferences.edit().putString("tts_history", historyJson).apply()
    }
    
    fun clearHistory() {
        preferences.edit().remove("tts_history").apply()
    }
}