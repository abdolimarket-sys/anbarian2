# اصلاح مرحله ۳۰ — هماهنگ‌سازی JVM و نسخه Gradle

این بسته دو اصلاح برای خطای ساخت GitHub Actions دارد:
1. در `app/build.gradle`، هدف Java و Kotlin هر دو روی JVM 17 قرار داده شده‌اند.
2. گردش‌کار GitHub از `gradle/gradle-build-action@v3` با Gradle 8.9 استفاده می‌کند؛ این نسخه با Android Gradle Plugin 8.7.3 سازگار است.

## نحوه استفاده
در مخزن `abdolimarket-sys/anbar_app`، این دو فایل را با نسخه‌های داخل بسته جایگزین کنید:
- `app/build.gradle`
- `.github/workflows/build-apk.yml`
سپس Commit changes کنید و از تب Actions، گردش‌کار Build Azar Anbar APK را دوباره اجرا کنید.

این اصلاح هنوز در اجرای واقعی GitHub تأیید نشده است. اگر خطای تازه‌ای آمد، گزارش جدید را بررسی کنید.
