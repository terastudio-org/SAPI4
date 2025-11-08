@echo off
echo SAPI4 Android App Build Script
echo.

REM Check if Android SDK and Java are installed
if not defined ANDROID_HOME (
    echo Error: ANDROID_HOME environment variable not set.
    echo Please set ANDROID_HOME to your Android SDK directory.
    echo Example: set ANDROID_HOME=C:\Users\YourName\AppData\Local\Android\Sdk
    pause
    exit /b 1
)

if not exist "%ANDROID_HOME%" (
    echo Error: Android SDK not found at %ANDROID_HOME%
    pause
    exit /b 1
)

REM Check for Java
where java >nul 2>&1
if errorlevel 1 (
    echo Error: Java not found. Please install Java JDK 8 or later and add it to PATH.
    pause
    exit /b 1
)

REM Navigate to the Android app directory
cd /d "%~dp0\android_app"

REM Check if Gradle wrapper exists
if not exist "gradlew.bat" (
    echo Error: Gradle wrapper not found. Building Android app requires gradlew.bat
    pause
    exit /b 1
)

REM Build the Android app
echo Building Android app...
call gradlew.bat assembleDebug

if errorlevel 1 (
    echo Error: Failed to build Android app
    pause
    exit /b 1
)

echo.
echo Android app build completed successfully!
echo APK file location: %~dp0\android_app\app\build\outputs\apk\debug\app-debug.apk
echo.

REM Option to install on connected device
set /p install_choice="Do you want to install the app on a connected device? (y/n): "
if /i "%install_choice%"=="y" (
    echo Installing app on connected device...
    "%ANDROID_HOME%\platform-tools\adb.exe" install app\build\outputs\apk\debug\app-debug.apk
    if errorlevel 1 (
        echo Warning: Failed to install app on device
    ) else (
        echo App installed successfully!
    )
)

pause