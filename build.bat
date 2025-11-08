@echo off
echo SAPI4 Build Script
echo.

REM Check if Visual Studio is installed
if exist "C:\Program Files\Microsoft Visual Studio\2022\Enterprise\VC\Auxiliary\Build\vcvars32.bat" (
    call "C:\Program Files\Microsoft Visual Studio\2022\Enterprise\VC\Auxiliary\Build\vcvars32.bat"
) else if exist "C:\Program Files (x86)\Microsoft Visual Studio\2019\Enterprise\VC\Auxiliary\Build\vcvars32.bat" (
    call "C:\Program Files (x86)\Microsoft Visual Studio\2019\Enterprise\VC\Auxiliary\Build\vcvars32.bat"
) else if exist "C:\Program Files\Microsoft Visual Studio\2019\Enterprise\VC\Auxiliary\Build\vcvars32.bat" (
    call "C:\Program Files\Microsoft Visual Studio\2019\Enterprise\VC\Auxiliary\Build\vcvars32.bat"
) else if exist "C:\Program Files (x86)\Microsoft Visual Studio\2017\Enterprise\VC\Auxiliary\Build\vcvars32.bat" (
    call "C:\Program Files (x86)\Microsoft Visual Studio\2017\Enterprise\VC\Auxiliary\Build\vcvars32.bat"
) else if exist "C:\Program Files\Microsoft Visual Studio\2017\Enterprise\VC\Auxiliary\Build\vcvars32.bat" (
    call "C:\Program Files\Microsoft Visual Studio\2017\Enterprise\VC\Auxiliary\Build\vcvars32.bat"
) else (
    echo Error: Could not find Visual Studio installation.
    echo Please install Visual Studio 2017 or later with C++ build tools.
    pause
    exit /b 1
)

echo Building SAPI4 library...
cl sapi4.cpp ole32.lib user32.lib /MT /LD -Ox -I"C:\Program Files (x86)\Microsoft Speech SDK\Include" /Fe:sapi4.dll
if errorlevel 1 (
    echo Error building sapi4.dll
    pause
    exit /b 1
)

echo Building sapi4out executable...
cl sapi4out.cpp ole32.lib user32.lib sapi4.lib /MT -Ox -I"C:\Program Files (x86)\Microsoft Speech SDK\Include" /Fe:sapi4out.exe
if errorlevel 1 (
    echo Error building sapi4out.exe
    pause
    exit /b 1
)

echo Building sapi4limits executable...
cl sapi4limits.cpp ole32.lib user32.lib sapi4.lib /MT -Ox -I"C:\Program Files (x86)\Microsoft Speech SDK\Include" /Fe:sapi4limits.exe
if errorlevel 1 (
    echo Error building sapi4limits.exe
    pause
    exit /b 1
)

echo.
echo Build completed successfully!
echo.

REM Create output directory if it doesn't exist
if not exist "build" mkdir build

REM Move built files to build directory
move sapi4.dll build\ >nul 2>&1
move sapi4out.exe build\ >nul 2>&1
move sapi4limits.exe build\ >nul 2>&1

echo Files copied to build directory.
pause