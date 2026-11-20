package com.example.labtarget
import android.app.Activity
import android.os.Bundle
import android.widget.*
class DemoActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val box=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(28,42,28,28) }
  box.addView(TextView(this).apply { text="Lab Target Demo"; textSize=24f })
  box.addView(TextView(this).apply { text="تطبيق تجريبي لاختبار أحداث الواجهة. لا تستخدم بيانات حقيقية." })
  box.addView(EditText(this).apply { hint="حقل تجريبي أول" })
  box.addView(EditText(this).apply { hint="حقل تجريبي ثانٍ" })
  setContentView(box)
 }
}
