# SAPI4 Android

An Android implementation of the SAPI4 text-to-speech system with an iOS-style user interface.

## Features

- Text-to-speech functionality using Android's built-in TTS engine
- iOS-style UI with rounded corners, appropriate colors, and tab-based navigation
- Voice selection from available system voices
- Adjustable pitch and speed controls
- History of previously spoken text
- Clean and modern interface

## Project Structure

```
android_app/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/sapi4android/
│   │   │   ├── MainActivity.kt          # Main activity with UI
│   │   │   ├── SAPI4Application.kt      # Application class managing TTS
│   │   │   ├── TTSManager.kt            # TTS functionality manager
│   │   │   ├── TTSHistoryItem.kt        # Data class for history items
│   │   │   └── HistoryAdapter.kt        # Adapter for history RecyclerView
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml    # Main UI layout
│   │   │   ├── drawable/
│   │   │   │   ├── ios_style_progress.xml  # iOS-style seekbar
│   │   │   │   ├── ios_thumb.xml           # iOS-style thumb for seekbar
│   │   │   │   ├── rounded_button.xml      # Rounded button style
│   │   │   │   ├── rounded_edittext.xml    # Rounded edit text style
│   │   │   │   ├── rounded_spinner.xml     # Rounded spinner style
│   │   │   │   ├── splash_screen.xml       # Splash screen
│   │   │   │   └── ic_launcher_foreground.xml  # Launcher icon
│   │   │   ├── mipmap-anydpi-v26/
│   │   │   │   ├── ic_launcher.xml
│   │   │   │   └── ic_launcher_round.xml
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   └── xml/
│   │   │       ├── backup_rules.xml
│   │   │       └── data_extraction_rules.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## Setup Instructions

1. Open Android Studio
2. Select "Open an existing project" and navigate to the `android_app` directory
3. Android Studio will automatically sync the project with Gradle
4. Make sure your SDK and build tools are up to date
5. Connect an Android device or start an emulator
6. Click "Run" to build and install the app

## Dependencies

- `androidx.core:core-ktx`
- `androidx.appcompat:appcompat` 
- `com.google.android.material:material`
- `androidx.constraintlayout:constraintlayout`
- `androidx.lifecycle:lifecycle-livedata-ktx`
- `androidx.lifecycle:lifecycle-viewmodel-ktx`
- `androidx.preference:preference-ktx`
- `androidx.recyclerview:recyclerview`
- `com.google.code.gson:gson`

## How to Use

1. Enter text in the text area
2. Select a voice from the dropdown
3. Adjust pitch and speed using the sliders
4. Click "Speak" to hear the text
5. Switch to the History tab to see previous entries
6. Click on history items to load them back into the form

## iOS-Style Elements

The app features several iOS-inspired UI elements:

- Rounded corners on input fields and buttons
- Blue color scheme similar to iOS defaults
- Tab-style navigation
- Clean, minimalist design
- Appropriate spacing and typography

## Architecture

The app follows a clean architecture pattern with:

- `SAPI4Application` managing the global TTS instance
- `TTSManager` encapsulating all TTS functionality
- `MainActivity` handling UI interactions
- Data persistence using SharedPreferences
- History management with RecyclerView