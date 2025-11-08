package com.example.sapi4android

import android.app.Application

class SAPI4Application : Application() {
    
    companion object {
        lateinit var ttsManager: TTSManager
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        
        ttsManager = TTSManager(this)
        ttsManager.initialize { success ->
            if (success) {
                // TTS initialized successfully
            } else {
                // Handle initialization failure
            }
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        ttsManager.shutdown()
    }
}