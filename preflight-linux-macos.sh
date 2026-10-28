#!/usr/bin/env bash
set -u
printf '\n=== بررسی پیش‌نیازهای ساخت APK انبارگردانی آذر ===\n'
fail=0
if command -v java >/dev/null 2>&1; then
  echo '[OK] Java موجود است:'
  java -version 2>&1 | head -n 3
else
  echo '[خطا] Java/JDK پیدا نشد.'; fail=1
fi
if command -v gradle >/dev/null 2>&1; then
  echo '[OK] Gradle موجود است:'
  gradle --version | head -n 8
else
  echo '[هشدار] دستور gradle پیدا نشد؛ پروژه Gradle Wrapper همراه ندارد.'; fail=1
fi
sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [ -n "$sdk" ] && [ -d "$sdk" ]; then
  echo "[OK] Android SDK: $sdk"
  if [ -d "$sdk/platforms/android-35" ]; then echo '[OK] Android Platform 35 موجود است.'; else echo '[هشدار] Android Platform 35 نصب نیست.'; fail=1; fi
  if [ -d "$sdk/build-tools" ]; then echo '[OK] پوشه Build Tools موجود است.'; else echo '[هشدار] Build Tools پیدا نشد.'; fail=1; fi
else
  echo '[خطا] ANDROID_HOME یا ANDROID_SDK_ROOT به SDK معتبر اشاره نمی‌کند.'; fail=1
fi
if [ -f app/src/main/assets/index.html ] && [ -f app/src/main/java/ir/azar/anbar/MainActivity.kt ]; then
  echo '[OK] فایل‌های اصلی برنامه وجود دارند.'
else
  echo '[خطا] فایل اصلی HTML یا MainActivity.kt موجود نیست.'; fail=1
fi
if [ "$fail" -eq 0 ]; then
  echo '\nپیش‌نیازهای پایه پیدا شدند. این بررسی، کامپایل موفق را تضمین نمی‌کند.'
  echo 'برای ساخت اجرا کنید: gradle assembleDebug'
  exit 0
else
  echo '\nهنوز یک یا چند پیش‌نیاز کم است. موارد هشدار/خطا را رفع کنید و دوباره اجرا کنید.'
  exit 2
fi
