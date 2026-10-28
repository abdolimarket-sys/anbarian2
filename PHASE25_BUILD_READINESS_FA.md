# مرحله ۲۵ — آماده‌سازی ساخت APK

## نتیجه واقعی این مرحله
- پروژه Android Studio از مرحله ۲۴ حفظ شده است.
- اسکریپت‌های راهنمای ساخت APK برای Windows و Linux/macOS اضافه شده‌اند.
- مسیر خروجی مورد انتظار APK دیباگ: `app/build/outputs/apk/debug/app-debug.apk`.
- در محیط فعلی این بسته، Java 21 موجود بود اما Gradle و Android SDK نصب/پیکربندی نبودند؛ بنابراین APK ساخته نشد و کامپایل اندروید تأیید نشده است.

## ساخت APK با Android Studio در ویندوز
1. فایل ZIP را استخراج کنید.
2. Android Studio را باز کرده و `Open` را بزنید؛ پوشه `AZAR_ANBAR_PHASE25_BUILD_READY` را انتخاب کنید.
3. اجازه دهید Gradle Sync تمام شود. اگر درخواست نصب SDK Platform 35 یا Build Tools شد، از SDK Manager نصب کنید.
4. از منو `Build > Build APK(s)` را انتخاب کنید.
5. پس از موفقیت، فایل را از `app/build/outputs/apk/debug/app-debug.apk` بردارید.
6. ابتدا روی گوشی آزمایشی نصب کنید و ذخیره آفلاین، ورود/خروج فایل، دوربین و بارکدخوان را تست کنید.

## ساخت با اسکریپت
- Windows: روی `build-debug-windows.bat` اجرا کنید؛ نیازمند JDK، Gradle و Android SDK تنظیم‌شده است.
- Linux/macOS: `./build-debug-linux-macos.sh` را اجرا کنید؛ نیازمند JDK، Gradle و Android SDK است.

## محدودیت‌های تأییدشده
- این ZIP سورس پروژه است، نه APK.
- اسکریپت‌ها فقط فرایند ساخت را اجرا می‌کنند و موفقیت ساخت را تضمین نمی‌کنند.
- ساخت واقعی، نصب گوشی، دوربین، بارکدخوان فیزیکی و فایل‌های واقعی هلو/سپیدار در این مرحله آزمایش نشده‌اند.
