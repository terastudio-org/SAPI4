@echo off
setlocal enabledelayedexpansion

echo SAPI4 Android App Advanced Build Script
echo ========================================
echo.

:menu
echo Select build option:
echo 1. Build Debug APK
echo 2. Build Release APK
echo 3. Build and Install on Device
echo 4. Clean Build
echo 5. Run Unit Tests
echo 6. Build AAB (Android App Bundle)
echo 7. Help
echo 8. Exit
echo.

set /p choice="Enter your choice (1-8): "

if "%choice%"=="1" goto build_debug
if "%choice%"=="2" goto build_release
if "%choice%"=="3" goto build_install
if "%choice%"=="4" goto clean_build
if "%choice%"=="5" goto run_tests
if "%choice%"=="6" goto build_bundle
if "%choice%"=="7" goto show_help
if "%choice%"=="8" goto exit_script

echo Invalid option, please try again.
goto menu

:build_debug
echo.
echo Building Debug APK...
cd /d "%~dp0"
if not exist "gradlew.bat" (
    echo Error: gradlew.bat not found in android_app directory
    pause
    exit /b 1
)
call gradlew.bat assembleDebug
if errorlevel 1 (
    echo Error: Failed to build Debug APK
    pause
    exit /b 1
)
echo.
echo Debug APK built successfully!
echo Location: app\build\outputs\apk\debug\app-debug.apk
pause
goto menu

:build_release
echo.
echo Building Release APK...
cd /d "%~dp0"
call gradlew.bat assembleRelease
if errorlevel 1 (
    echo Error: Failed to build Release APK
    pause
    exit /b 1
)
echo.
echo Release APK built successfully!
echo Location: app\build\outputs\apk\release\app-release.apk
pause
goto menu

:build_install
echo.
echo Building and Installing on Device...
cd /d "%~dp0"
call gradlew.bat installDebug
if errorlevel 1 (
    echo Error: Failed to build or install on device
    echo Make sure a device is connected and USB debugging is enabled
    pause
    exit /b 1
)
echo.
echo App installed successfully on connected device!
pause
goto menu

:clean_build
echo.
echo Cleaning build...
cd /d "%~dp0"
call gradlew.bat clean
echo.
echo Build cleaned successfully!
pause
goto menu

:run_tests
echo.
echo Running unit tests...
cd /d "%~dp0"
call gradlew.bat test
if errorlevel 1 (
    echo Some tests failed or error occurred
) else (
    echo All tests passed!
)
pause
goto menu

:build_bundle
echo.
echo Building Android App Bundle...
cd /d "%~dp0"
call gradlew.bat bundleRelease
if errorlevel 1 (
    echo Error: Failed to build AAB
    pause
    exit /b 1
)
echo.
echo Android App Bundle built successfully!
echo Location: app\build\outputs\bundle\release\app-release.aab
pause
goto menu

:show_help
echo.
echo This script builds the SAPI4 Android application.
echo.
echo Prerequisites:
echo - Android SDK installed and ANDROID_HOME set
echo - Java JDK 8 or later
echo - Android device connected with USB debugging enabled (for install option)
echo - Gradle wrapper (gradlew.bat) present in this directory
echo.
pause
goto menu

:exit_script
echo.
echo Exiting build script.
exit /b 0