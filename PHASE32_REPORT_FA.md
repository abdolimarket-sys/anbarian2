# گزارش مرحله ۳۲ — اصلاح ساخت ابری

## اصلاح‌های مشخص
- ارتقای `android-actions/setup-android` از v3 به v4؛ نسخه v3 درخواست بسته حذف‌شدهٔ SDK با نام `tools` را می‌کرد و باعث شکست مرحلهٔ SDK می‌شد.
- نصب صریح `platform-tools`، `platforms;android-35` و `build-tools;35.0.0`.
- هماهنگ‌سازی هدف Java و Kotlin روی JVM 17 در `app/build.gradle`.
- تعیین Gradle 8.9 برای سازگاری با Android Gradle Plugin 8.7.3.
- اضافه‌شدن بررسی‌های پیش از کامپایل و بارگذاری APK به‌عنوان Artifact.

## وضعیت اعتبارسنجی
- فایل‌های پروژه و تنظیمات workflow بازبینی شدند.
- ZIP پس از ساخت با `unzip -t` بررسی می‌شود.
- ساخت واقعی APK در این محیط اجرا نشده؛ نتیجهٔ نهایی فقط پس از اجرای GitHub Actions معلوم می‌شود.
