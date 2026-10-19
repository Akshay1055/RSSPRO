@echo off
echo Starting Android Studio with Auto-Cleanup on Exit...

:: Locate Android Studio Executable
set "STUDIO_PATH=D:\Program Files\Android\Android Studio\bin\studio64.exe"
if not exist "%STUDIO_PATH%" (
    set "STUDIO_PATH=C:\Program Files\Android\Android Studio\bin\studio64.exe"
)

if not exist "%STUDIO_PATH%" (
    echo Error: studio64.exe not found! Please check your Android Studio installation path.
    pause
    exit /b
)

:: Launch Android Studio and wait for it to close
echo Launching Android Studio...
start /wait "" "%STUDIO_PATH%"

:: After Android Studio closes, run cleanup
echo.
echo Android Studio has closed. Running automatic background cleanup...

call "%~dp0cleanup_background.bat"
