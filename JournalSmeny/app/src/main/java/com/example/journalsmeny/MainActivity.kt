package com.example.journalsmeny

import android.app.Activity
import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.text.InputType
import android.widget.*
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var start1: EditText
    private lateinit var start2: EditText
    private lateinit var startOil: EditText
    private lateinit var in1: EditText
    private lateinit var out1: EditText
    private lateinit var in2: EditText
    private lateinit var out2: EditText
    private lateinit var inOil: EditText
    private lateinit var outOil: EditText
    private lateinit var end1: TextView
    private lateinit var end2: TextView
    private lateinit var endTotal: TextView
    private lateinit var endOil: TextView
    private lateinit var startTotal: TextView

    private val blue = Color.rgb(23, 105, 170)
    private val dark = Color.rgb(31, 41, 55)
    private val muted = Color.rgb(100, 116, 139)
    private val bg = Color.rgb(244, 247, 250)
    private val df = DecimalFormat("#,##0.###", DecimalFormatSymbols(Locale("ru", "RU")))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = Color.WHITE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        buildUi()
        calculate()
    }

    private fun buildUi() {
        val scroll = ScrollView(this).apply { setBackgroundColor(bg); isFillViewport = true }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(28))
        }
        scroll.addView(root)
        setContentView(scroll)

        root.addView(TextView(this).apply {
            text = "ЖУРНАЛ СМЕНЫ"
            textSize = 25f; setTextColor(dark); setTypeface(null, Typeface.BOLD)
        })
        root.addView(TextView(this).apply {
            text = "Введите остатки на начало смены и движение материалов. Итог считается автоматически. Данные не сохраняются."
            textSize = 14f; setTextColor(muted); setPadding(0, dp(6), 0, dp(14))
        })

        root.addView(sectionTitle("1. НАЧАЛО СМЕНЫ"))
        start1 = addInputRow(root, "Бункер № 1", "т")
        start2 = addInputRow(root, "Бункер № 2", "т")
        startTotal = addResultRow(root, "Всего в двух бункерах", "т", true)
        startOil = addInputRow(root, "Силиконовое масло", "л")

        root.addView(sectionTitle("2. ИЗМЕНЕНИЯ ЗА СМЕНУ"))
        root.addView(TextView(this).apply {
            text = "Укажите поступление и расход. Если движения не было, оставьте поле пустым."
            textSize = 13f; setTextColor(muted); setPadding(0, 0, 0, dp(8))
        })
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(TextView(this@MainActivity).apply { text = "Материал"; setTextColor(muted); textSize = 12f; layoutParams = LinearLayout.LayoutParams(0, dp(28), 1.3f) })
            addView(TextView(this@MainActivity).apply { text = "Поступило"; setTextColor(muted); textSize = 12f; gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(0, dp(28), 1f) })
            addView(TextView(this@MainActivity).apply { text = "Расход"; setTextColor(muted); textSize = 12f; gravity = Gravity.CENTER; layoutParams = LinearLayout.LayoutParams(0, dp(28), 1f) })
        })
        val row1 = addMovementRow(root, "Бункер № 1", "т")
        in1 = row1.first; out1 = row1.second
        val row2 = addMovementRow(root, "Бункер № 2", "т")
        in2 = row2.first; out2 = row2.second
        val rowOil = addMovementRow(root, "Силикон, л", "л")
        inOil = rowOil.first; outOil = rowOil.second

        root.addView(sectionTitle("3. КОНЕЦ СМЕНЫ"))
        end1 = addResultRow(root, "Бункер № 1", "т", false)
        end2 = addResultRow(root, "Бункер № 2", "т", false)
        endTotal = addResultRow(root, "Всего в двух бункерах", "т", true)
        endOil = addResultRow(root, "Силиконовое масло", "л", false)

        val calc = Button(this).apply {
            text = "ПЕРЕСЧИТАТЬ"
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue))
            setOnClickListener { calculate(); hideKeyboard() }
        }
        root.addView(calc, LinearLayout.LayoutParams(-1, dp(52)).apply { topMargin = dp(16) })
        val clear = Button(this).apply {
            text = "ОЧИСТИТЬ ВСЕ ПОЛЯ"
            isAllCaps = false
            setOnClickListener { clearAll() }
        }
        root.addView(clear, LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(6) })

        listOf(start1, start2, startOil, in1, out1, in2, out2, inOil, outOil).forEach { field ->
            field.addTextChangedListener(SimpleTextWatcher { calculate() })
        }
    }

    private fun sectionTitle(text: String) = TextView(this).apply {
        this.text = text; textSize = 15f; setTextColor(blue); setTypeface(null, Typeface.BOLD)
        setPadding(0, dp(18), 0, dp(8))
    }

    private fun addInputRow(parent: LinearLayout, label: String, unit: String): EditText {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(12), dp(4), dp(12), dp(4)); background = rounded(Color.WHITE, 12); layoutParams = LinearLayout.LayoutParams(-1, dp(58)).apply { bottomMargin = dp(6) } }
        row.addView(TextView(this).apply { text = label; textSize = 15f; setTextColor(dark) }, LinearLayout.LayoutParams(0, -2, 1f))
        val input = numberInput("0").apply { layoutParams = LinearLayout.LayoutParams(dp(115), dp(46)); gravity = Gravity.END or Gravity.CENTER_VERTICAL }
        row.addView(input)
        row.addView(TextView(this).apply { text = " $unit"; textSize = 13f; setTextColor(muted) })
        parent.addView(row)
        return input
    }

    private fun addMovementRow(parent: LinearLayout, label: String, unit: String): Pair<EditText, EditText> {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; background = rounded(Color.WHITE, 12); setPadding(dp(10), dp(7), dp(10), dp(7)); layoutParams = LinearLayout.LayoutParams(-1, dp(78)).apply { bottomMargin = dp(6) } }
        box.addView(TextView(this).apply { text = label; textSize = 14f; setTextColor(dark); setTypeface(null, Typeface.BOLD); setPadding(0, 0, 0, dp(4)) })
        val fields = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val plus = numberInput("0").apply { layoutParams = LinearLayout.LayoutParams(0, dp(42), 1f); hint = "0" }
        val minus = numberInput("0").apply { layoutParams = LinearLayout.LayoutParams(0, dp(42), 1f); hint = "0" }
        fields.addView(plus)
        fields.addView(Space(this), LinearLayout.LayoutParams(dp(8), 1))
        fields.addView(minus)
        box.addView(fields)
        parent.addView(box)
        return Pair(plus, minus)
    }

    private fun addResultRow(parent: LinearLayout, label: String, unit: String, emphasized: Boolean): TextView {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = rounded(if (emphasized) Color.rgb(225, 239, 251) else Color.WHITE, 12)
            layoutParams = LinearLayout.LayoutParams(-1, dp(if (emphasized) 58 else 52)).apply { bottomMargin = dp(6) }
        }
        row.addView(TextView(this).apply { text = label; textSize = if (emphasized) 15f else 14f; setTextColor(dark); if (emphasized) setTypeface(null, Typeface.BOLD) }, LinearLayout.LayoutParams(0, -2, 1f))
        val value = TextView(this).apply { text = "0 $unit"; textSize = if (emphasized) 18f else 16f; setTextColor(if (emphasized) blue else dark); setTypeface(null, Typeface.BOLD); gravity = Gravity.END }
        row.addView(value)
        parent.addView(row)
        return value
    }

    private fun numberInput(hintText: String) = EditText(this).apply {
        hint = hintText; textSize = 16f; setTextColor(dark); setPadding(dp(8), 0, dp(8), 0)
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
        singleLine = true
        background = rounded(Color.rgb(246, 248, 251), 8)
    }

    private fun calculate() {
        if (!::start1.isInitialized) return
        val s1 = value(start1); val s2 = value(start2); val so = value(startOil)
        val e1 = s1 + value(in1) - value(out1)
        val e2 = s2 + value(in2) - value(out2)
        val eo = so + value(inOil) - value(outOil)
        startTotal.text = "${fmt(s1 + s2)} т"
        end1.text = "${fmt(e1)} т"
        end2.text = "${fmt(e2)} т"
        endTotal.text = "${fmt(e1 + e2)} т"
        endOil.text = "${fmt(eo)} л"
        end1.setTextColor(if (e1 < 0) Color.RED else dark)
        end2.setTextColor(if (e2 < 0) Color.RED else dark)
        endOil.setTextColor(if (eo < 0) Color.RED else dark)
        endTotal.setTextColor(if (e1 + e2 < 0) Color.RED else blue)
    }

    private fun value(edit: EditText): Double = edit.text.toString().trim().replace(',', '.').toDoubleOrNull() ?: 0.0
    private fun fmt(n: Double): String = df.format(if (kotlin.math.abs(n) < 0.0005) 0.0 else n)
    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
    private fun hideKeyboard() { (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(window.decorView.windowToken, 0) }
    private fun clearAll() {
        listOf(start1, start2, startOil, in1, out1, in2, out2, inOil, outOil).forEach { it.setText("") }
        calculate()
    }
}

private class SimpleTextWatcher(val changed: () -> Unit) : android.text.TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { changed() }
    override fun afterTextChanged(s: android.text.Editable?) {}
}
