package com.nexora.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : Activity() {
    private lateinit var display: TextView
    private var expression = ""
    private var fresh = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(13, 21, 35)
        window.navigationBarColor = android.graphics.Color.rgb(13, 21, 35)
        val bg = 0xFF0D1523.toInt()
        val panel = 0xFF19283B.toInt()
        val gold = 0xFFE5AD50.toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 26, 20, 18)
            setBackgroundColor(bg)
        }
        val brand = TextView(this).apply {
            text = "N E X O R A"; textSize = 23f; setTextColor(gold)
            gravity = Gravity.CENTER; setPadding(0, 8, 0, 20)
        }
        display = TextView(this).apply {
            text = "0"; textSize = 38f; gravity = Gravity.END or Gravity.CENTER_VERTICAL
            setTextColor(-1); setPadding(16, 18, 16, 18); setBackgroundColor(panel)
        }
        root.addView(brand, LinearLayout.LayoutParams(-1, 58))
        root.addView(display, LinearLayout.LayoutParams(-1, 0, 1f))
        val keys = arrayOf(
            arrayOf("AC", "±", "%", "÷"), arrayOf("7", "8", "9", "×"),
            arrayOf("4", "5", "6", "−"), arrayOf("1", "2", "3", "+"),
            arrayOf("⌫", "0", ".", "=")
        )
        keys.forEach { row ->
            val line = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            row.forEach { key ->
                val b = Button(this).apply {
                    text = key; textSize = 20f; setTextColor(-1)
                    setBackgroundColor(if (key in listOf("÷","×","−","+","=")) 0xFFB8792C.toInt() else if (key in listOf("AC","±","%","⌫")) 0xFF34465E.toInt() else panel)
                    setOnClickListener { press(key) }
                }
                line.addView(b, LinearLayout.LayoutParams(0, 62, 1f).apply { setMargins(4,5,4,5) })
            }
            root.addView(line)
        }
        root.addView(Button(this).apply {
            text = "LAB SETTINGS · إعدادات المختبر"; setTextColor(gold); setBackgroundColor(panel)
            setOnClickListener { startActivity(Intent(this@MainActivity, LabSettingsActivity::class.java)) }
        }, LinearLayout.LayoutParams(-1, 52))
        setContentView(root)
    }

    private fun press(k: String) {
        when (k) {
            "AC" -> { expression = ""; fresh = true; display.text = "0" }
            "⌫" -> { expression = expression.dropLast(1); display.text = expression.ifEmpty { "0" } }
            "±" -> { if (expression.startsWith("-")) expression = expression.drop(1) else if (expression.isNotEmpty()) expression = "-$expression"; display.text = expression }
            "%" -> { expression.toDoubleOrNull()?.let { expression = (it / 100.0).toString(); display.text = expression } }
            "=" -> calculate()
            else -> {
                if (fresh && k.firstOrNull()?.isDigit() == true) expression = ""
                fresh = false
                expression += when (k) { "×" -> "*"; "÷" -> "/"; "−" -> "-"; else -> k }
                display.text = expression
            }
        }
    }

    private fun calculate() {
        try {
            val parts = Regex("(?<=[+\\-*/])|(?=[+\\-*/])").split(expression).filter { it.isNotBlank() }
            if (parts.isEmpty()) return
            var result = parts[0].toBigDecimal()
            var i = 1
            while (i + 1 < parts.size) {
                val v = parts[i + 1].toBigDecimal()
                result = when (parts[i]) {
                    "+" -> result + v
                    "-" -> result - v
                    "*" -> result * v
                    "/" -> result.divide(v, 8, RoundingMode.HALF_UP)
                    else -> result
                }
                i += 2
            }
            expression = result.stripTrailingZeros().toPlainString()
            display.text = expression; fresh = true
        } catch (_: Exception) { display.text = "Error"; expression = ""; fresh = true }
    }
}
