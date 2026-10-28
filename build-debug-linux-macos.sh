#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if ! command -v java >/dev/null 2>&1; then
  echo "ERROR: Install JDK 17 or 21 and configure JAVA_HOME." >&2; exit 1
fi
if ! command -v gradle >/dev/null 2>&1; then
  echo "ERROR: Gradle is not installed. Open this project in Android Studio or install Gradle." >&2; exit 2
fi
if [[ -z "${ANDROID_HOME:-}" && -z "${ANDROID_SDK_ROOT:-}" ]]; then
  echo "WARNING: Android SDK environment variable is not set; Gradle may fail." >&2
fi
gradle --no-daemon assembleDebug
echo "APK: app/build/outputs/apk/debug/app-debug.apk"
