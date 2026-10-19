@echo off
echo ===================================================
echo   Cleaning up Android Studio Background Processes
echo ===================================================

echo [1/4] Stopping Gradle Daemons...
call gradlew.bat --stop 2>NUL

echo [2/4] Terminating Kotlin & Java Compiler Daemons...
wmic process where "name='java.exe' and commandline like '%%gradle%%'" call terminate 2>NUL
wmic process where "name='java.exe' and commandline like '%%kotlin%%'" call terminate 2>NUL

echo [3/4] Stopping ADB Daemon...
taskkill /F /IM adb.exe /T 2>NUL

echo [4/4] Closing Emulator Processes (if any)...
taskkill /F /IM qemu-system-x86_64.exe /T 2>NUL
taskkill /F /IM emulator.exe /T 2>NUL

echo ===================================================
echo   Cleanup Complete! All background processes closed.
echo ===================================================
pause
