package com.nexora.app
import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import kotlin.concurrent.thread

class LabAccessibilityService : AccessibilityService() {
 private val allowedPackage = "com.example.labtarget"
 private var lastSent = 0L
 override fun onAccessibilityEvent(event: AccessibilityEvent?) {
  event ?: return
  if (event.packageName?.toString() != allowedPackage) return
  val now = System.currentTimeMillis()
  if (now-lastSent < 250) return
  val name = when(event.eventType) {
   AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "WINDOW_STATE_CHANGED"
   AccessibilityEvent.TYPE_VIEW_FOCUSED -> "VIEW_FOCUSED"
   AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> "VIEW_TEXT_CHANGED"
   else -> return
  }
  lastSent = now
  // Do not inspect event.text, source nodes, or field contents.
  val host = getSharedPreferences("nexora", MODE_PRIVATE).getString("host","") ?: ""
  if (host.isBlank()) return
  thread {
   try {
    val conn = URL("http://$host:8765/event").openConnection() as HttpURLConnection
    conn.requestMethod="POST"; conn.connectTimeout=2500; conn.readTimeout=2500; conn.doOutput=true
    conn.setRequestProperty("Content-Type","application/json")
    val body = JSONObject().put("package",allowedPackage).put("event",name).toString()
    conn.outputStream.use { it.write(body.toByteArray()) }
    conn.inputStream.close(); conn.disconnect()
   } catch (_: Exception) {}
  }
 }
 override fun onInterrupt() {}
}
