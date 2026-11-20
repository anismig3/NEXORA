# NEXORA — Calculator & Android Lab

المشروع يحتوي على آلة حاسبة Android باسم NEXORA، وصفحة إعدادات مختبر معلنة، وخدمة Accessibility محصورة بتطبيق Demo مرفق، ولوحة Windows.

## بناء APK
افتح `android` في Android Studio ثم Build > Build APK(s).
كرر ذلك مع `demo_target` لبناء تطبيق الاختبار.

## تشغيل لوحة Windows
داخل `desktop`:
```powershell
py -m venv .venv
.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python dashboard.py
```
لبناء EXE:
```powershell
pip install pyinstaller
pyinstaller --onefile --noconsole --name NEXORA-Dashboard dashboard.py
```

## ربط الهاتف
1. شغّل الخادم من لوحة Windows.
2. اعرف IPv4 للحاسوب بواسطة `ipconfig`.
3. أدخل عنوان IP في NEXORA > Lab Settings.
4. فعّل خدمة NEXORA Lab يدوياً من إعدادات Accessibility.
5. افتح Lab Target Demo وجرب حقول النص التجريبية.

خدمة Accessibility تسمح فقط بالحزمة `com.example.labtarget`، وترسل نوع الحدث واسم الحزمة فقط. لا تقرأ النصوص أو محتوى الرسائل أو كلمات المرور.
