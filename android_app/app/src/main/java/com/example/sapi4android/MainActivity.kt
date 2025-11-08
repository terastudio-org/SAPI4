package com.example.sapi4android

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var textInput: EditText
    private lateinit var voiceSpinner: Spinner
    private lateinit var pitchSeekBar: SeekBar
    private lateinit var speedSeekBar: SeekBar
    private lateinit var speakButton: Button
    private lateinit var saveButton: Button
    private lateinit var tabSpeak: TextView
    private lateinit var tabHistory: TextView
    private lateinit var speakContent: ScrollView
    private lateinit var historyContent: LinearLayout
    private lateinit var recyclerViewHistory: androidx.recyclerview.widget.RecyclerView
    private lateinit var clearHistoryButton: Button
    
    private var availableVoices: List<String> = emptyList()
    private val ttsManager by lazy { SAPI4Application.ttsManager }
    private lateinit var historyAdapter: HistoryAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupVoiceSpinner()
        setupListeners()
        setupTabLayout()
        setupHistoryTab()
        
        // Show initialization status
        if (ttsManager.isInitialized) {
            Toast.makeText(this, "TTS Ready", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "TTS Initializing...", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun initializeViews() {
        textInput = findViewById(R.id.textInput)
        voiceSpinner = findViewById(R.id.voiceSpinner)
        pitchSeekBar = findViewById(R.id.pitchSeekBar)
        speedSeekBar = findViewById(R.id.speedSeekBar)
        speakButton = findViewById(R.id.speakButton)
        saveButton = findViewById(R.id.saveButton)
        tabSpeak = findViewById(R.id.tab_speak)
        tabHistory = findViewById(R.id.tab_history)
        speakContent = findViewById(R.id.speak_content)
        historyContent = findViewById(R.id.history_content)
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        
        // Initialize RecyclerView
        historyAdapter = HistoryAdapter(mutableListOf()) { historyItem ->
            // When a history item is clicked, populate the main form with its values
            textInput.setText(historyItem.text)
            pitchSeekBar.progress = (historyItem.pitch * 100).toInt()
            speedSeekBar.progress = (historyItem.speed * 100).toInt()
            
            // Find and select the voice in the spinner
            val voiceIndex = availableVoices.indexOf(historyItem.voice)
            if (voiceIndex != -1) {
                voiceSpinner.setSelection(voiceIndex)
            }
            
            // Switch to the speak tab
            showSpeakTab()
        }
        
        recyclerViewHistory.adapter = historyAdapter
        recyclerViewHistory.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
    }
    
    private fun setupVoiceSpinner() {
        availableVoices = ttsManager.getAvailableVoices()
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, availableVoices)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        voiceSpinner.adapter = adapter
        
        // Set default selection
        if (availableVoices.isNotEmpty()) {
            voiceSpinner.setSelection(0)
        }
    }
    
    private fun setupTabLayout() {
        // Initially show the speak tab
        showSpeakTab()
        
        tabSpeak.setOnClickListener {
            showSpeakTab()
        }
        
        tabHistory.setOnClickListener {
            showHistoryTab()
        }
    }
    
    private fun setupHistoryTab() {
        clearHistoryButton.setOnClickListener {
            ttsManager.clearHistory()
            historyAdapter.updateHistory(emptyList())
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showSpeakTab() {
        speakContent.visibility = View.VISIBLE
        historyContent.visibility = View.GONE
        tabSpeak.setTextColor(getColor(R.color.purple_500))
        tabHistory.setTextColor(getColor(R.color.teal_200))
    }
    
    private fun showHistoryTab() {
        speakContent.visibility = View.GONE
        historyContent.visibility = View.VISIBLE
        tabSpeak.setTextColor(getColor(R.color.teal_200))
        tabHistory.setTextColor(getColor(R.color.purple_500))
        
        // Load and display history
        loadHistory()
    }
    
    private fun loadHistory() {
        val historyItems = ttsManager.getHistoryItems()
        historyAdapter.updateHistory(historyItems)
    }
    
    private fun setupListeners() {
        speakButton.setOnClickListener {
            speakText()
        }
        
        saveButton.setOnClickListener {
            // For now, just speak the text - saving functionality would require additional implementation
            speakText()
            Toast.makeText(this, "Audio saved functionality to be implemented", Toast.LENGTH_SHORT).show()
        }
        
        pitchSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update pitch in real-time if TTS is initialized
                if (ttsManager.isInitialized) {
                    ttsManager.setPitch(progress.toFloat() / 100f)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update speed in real-time if TTS is initialized
                if (ttsManager.isInitialized) {
                    ttsManager.setSpeed(progress.toFloat() / 100f)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    private fun speakText() {
        val text = textInput.text.toString().trim()
        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter text to speak", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Get selected voice if available
        val selectedVoiceName = if (availableVoices.isNotEmpty() && voiceSpinner.selectedItemPosition >= 0) {
            availableVoices[voiceSpinner.selectedItemPosition]
        } else {
            null
        }
        
        // Get pitch and speed from seekbars
        val pitch = pitchSeekBar.progress.toFloat() / 100f
        val speed = speedSeekBar.progress.toFloat() / 100f
        
        // Speak the text
        ttsManager.speak(text, pitch, speed, selectedVoiceName)
        
        Toast.makeText(this, "Speaking...", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Don't shutdown the TTS manager here since it's managed by the Application class
    }
}