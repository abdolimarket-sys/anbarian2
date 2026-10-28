@echo off
setlocal
cd /d "%~dp0"
echo =============================================
echo AZAR ANBAR - DEBUG APK BUILDER
echo =============================================
where java >nul 2>nul
if errorlevel 1 (
  echo ERROR: Java JDK not found. Install JDK 17 or 21 and set JAVA_HOME.
  pause
  exit /b 1
)
where gradle >nul 2>nul
if errorlevel 1 (
  echo ERROR: Gradle command not found.
  echo Open this folder in Android Studio and use Build ^> Build APK(s),
  echo or install Gradle and run this script again.
  pause
  exit /b 2
)
if "%ANDROID_HOME%"=="" if "%ANDROID_SDK_ROOT%"=="" (
  echo WARNING: ANDROID_HOME / ANDROID_SDK_ROOT is not set.
  echo Gradle may fail unless Android SDK is configured in Android Studio.
)
gradle --version
if errorlevel 1 exit /b 3
gradle --no-daemon assembleDebug
if errorlevel 1 (
  echo BUILD FAILED. See the Gradle error above.
  pause
  exit /b 4
)
echo BUILD SUCCESS.
echo APK path: app\build\outputs\apk\debug\app-debug.apk
pause
