package com.nexora.app
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.*

class LabSettingsActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val prefs = getSharedPreferences("nexora", MODE_PRIVATE)
  val layout = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(28,40,28,24); setBackgroundColor(0xFF0D1523.toInt()) }
  layout.addView(TextView(this).apply { text="NEXORA LAB SETTINGS"; textSize=22f; setTextColor(0xFFE5AD50.toInt()) })
  layout.addView(TextView(this).apply { text="مختبر معلن لاختبار Lab Target Demo فقط. لا يتم جمع نصوص الحقول أو الرسائل."; textSize=16f; setTextColor(-1); setPadding(0,20,0,20) })
  val host = EditText(this).apply { hint="عنوان IPv4 للحاسوب"; setSingleLine(); setTextColor(-1); setHintTextColor(0xFF9AABC0.toInt()); setText(prefs.getString("host","")) }
  layout.addView(host)
  layout.addView(Button(this).apply { text="حفظ عنوان الحاسوب"; setOnClickListener { prefs.edit().putString("host",host.text.toString().trim()).apply(); Toast.makeText(this@LabSettingsActivity,"تم الحفظ",Toast.LENGTH_SHORT).show() } })
  layout.addView(Button(this).apply { text="فتح إعدادات Accessibility"; setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) } })
  setContentView(layout)
 }
}
