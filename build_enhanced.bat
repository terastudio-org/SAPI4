@echo off
setlocal enabledelayedexpansion

echo SAPI4 Enhanced Build Script
echo ==========================
echo.

REM Check for required tools and dependencies
echo Checking prerequisites...

REM Check for Visual Studio
set VS_FOUND=0
for %%V in ("2022", "2019", "2017") do (
    for %%R in ("Enterprise", "Professional", "Community") do (
        if exist "C:\Program Files\Microsoft Visual Studio\%%V\%%R\VC\Auxiliary\Build\vcvars32.bat" (
            set "VCVARS_PATH=C:\Program Files\Microsoft Visual Studio\%%V\%%R\VC\Auxiliary\Build\vcvars32.bat"
            set VS_FOUND=1
            goto vs_found
        )
        if exist "C:\Program Files (x86)\Microsoft Visual Studio\%%V\%%R\VC\Auxiliary\Build\vcvars32.bat" (
            set "VCVARS_PATH=C:\Program Files (x86)\Microsoft Visual Studio\%%V\%%R\VC\Auxiliary\Build\vcvars32.bat"
            set VS_FOUND=1
            goto vs_found
        )
    )
)

:vs_found
if !VS_FOUND! == 0 (
    echo Error: Could not find Visual Studio installation.
    echo Please install Visual Studio 2017 or later with C++ build tools.
    pause
    exit /b 1
)

REM Check for Windows SDK
set SDK_FOUND=0
if exist "C:\Program Files (x86)\Windows Kits\10\Include" (
    set SDK_FOUND=1
) else if exist "C:\Program Files\Windows Kits\10\Include" (
    set SDK_FOUND=1
)

if !SDK_FOUND! == 0 (
    echo Warning: Windows 10 SDK not found. Build may fail.
)

REM Check for Microsoft Speech SDK
set SPEECH_SDK_PATH=
if exist "C:\Program Files (x86)\Microsoft Speech SDK\Include\sapi.h" (
    set "SPEECH_SDK_PATH=C:\Program Files (x86)\Microsoft Speech SDK"
) else if exist "C:\Program Files\Microsoft Speech SDK\Include\sapi.h" (
    set "SPEECH_SDK_PATH=C:\Program Files\Microsoft Speech SDK"
)

if "!SPEECH_SDK_PATH!" == "" (
    echo Error: Microsoft Speech SDK not found.
    echo Please install Microsoft Speech SDK 4.0.
    pause
    exit /b 1
)

echo Prerequisites check completed.
echo.

REM Setup VS environment
call "!VCVARS_PATH!"
if errorlevel 1 (
    echo Error: Failed to initialize Visual Studio environment
    pause
    exit /b 1
)

REM Create build directory
if not exist "build" mkdir build

REM Build configuration
set BUILD_TYPE=Release
set INCLUDE_DIR="!SPEECH_SDK_PATH!\Include"
set LIBS=ole32.lib user32.lib
set CFLAGS=/MT /Ox /DNDEBUG

echo Building SAPI4 components...
echo.

echo Compiling sapi4.dll...
cl sapi4.cpp %LIBS% /LD %CFLAGS% /I%INCLUDE_DIR% /Fe:build\sapi4.dll /Fo:build\sapi4.obj
if errorlevel 1 (
    echo Error: Failed to build sapi4.dll
    pause
    exit /b 1
)

echo Compiling sapi4out.exe...
cl sapi4out.cpp %LIBS% %CFLAGS% /I%INCLUDE_DIR% /Fe:build\sapi4out.exe /Fo:build\sapi4out.obj /link /LIBPATH:"build"
if errorlevel 1 (
    echo Error: Failed to build sapi4out.exe
    pause
    exit /b 1
)

echo Compiling sapi4limits.exe...
cl sapi4limits.cpp %LIBS% %CFLAGS% /I%INCLUDE_DIR% /Fe:build\sapi4limits.exe /Fo:build\sapi4limits.obj /link /LIBPATH:"build"
if errorlevel 1 (
    echo Error: Failed to build sapi4limits.exe
    pause
    exit /b 1
)

REM Copy required files to build directory
copy "sapi.h" "build\" >nul 2>&1
copy "sapi4.hpp" "build\" >nul 2>&1

echo.
echo Build completed successfully!
echo Output files are in the 'build' directory:
echo - sapi4.dll
echo - sapi4out.exe
echo - sapi4limits.exe
echo.

REM Build web server if D compiler is available
echo Checking for D compiler...
where ldc2 >nul 2>&1
if not errorlevel 1 (
    where dub >nul 2>&1
    if not errorlevel 1 (
        echo.
        echo Building SAPI4 web server with D...
        if exist "SAPI4_web" (
            cd SAPI4_web
            dub --compiler=ldc2 --arch=x86 --build=release
            if errorlevel 1 (
                echo Warning: Failed to build web server
            ) else (
                echo Web server built successfully!
            )
            cd ..
        )
    ) else (
        echo D compiler found but dub not found. Install dub to build web server.
    )
) else (
    echo D compiler (ldc2) not found. Skipping web server build.
    echo Install ldc2 and dub to build the web server component.
)

echo.
echo Build process finished.
pause