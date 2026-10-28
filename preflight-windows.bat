@echo off
setlocal
set "FAIL=0"
echo === بررسی پیش‌نیازهای ساخت APK انبارگردانی آذر ===
where java >nul 2>nul
if errorlevel 1 (echo [خطا] Java/JDK پیدا نشد.& set "FAIL=1") else (echo [OK] Java موجود است.& java -version)
where gradle >nul 2>nul
if errorlevel 1 (echo [هشدار] Gradle پیدا نشد. این پروژه Gradle Wrapper همراه ندارد.& set "FAIL=1") else (echo [OK] Gradle موجود است.& gradle --version)
if defined ANDROID_HOME (set "SDK=%ANDROID_HOME%") else (set "SDK=%ANDROID_SDK_ROOT%")
if not defined SDK (echo [خطا] ANDROID_HOME یا ANDROID_SDK_ROOT تنظیم نشده است.& set "FAIL=1") else (
  if exist "%SDK%\platforms\android-35" (echo [OK] Android Platform 35 موجود است.) else (echo [هشدار] Android Platform 35 نصب نیست.& set "FAIL=1")
  if exist "%SDK%\build-tools" (echo [OK] Build Tools موجود است.) else (echo [هشدار] Build Tools پیدا نشد.& set "FAIL=1")
)
if exist "app\src\main\assets\index.html" (echo [OK] فایل برنامه وجود دارد.) else (echo [خطا] index.html پیدا نشد.& set "FAIL=1")
if "%FAIL%"=="0" (echo پیش‌نیازهای پایه پیدا شدند. سپس اجرا کنید: gradle assembleDebug) else (echo پیش‌نیازهای لازم کامل نیستند.)
echo این ابزار فقط پیش‌نیازها را بررسی می‌کند و APK نمی‌سازد.
pause
exit /b %FAIL%
