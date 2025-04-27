package com.example.monetaryconversionapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private val exchangeRates = mapOf(
        "USD" to 1f,
        "VND" to 25953.4f,
        "EUR" to 29681.0f,
        "JPY" to 181.11f,
        "GBP" to 34642.2f,
        "CNY" to 3570.58f,
        "KRW" to 18.09f,
        "SGD" to 19801.9f,
        "THB" to 769.0f,
        "CHF" to 31430.7f
    )

    private val currencySymbols = mapOf(
        "USD" to "$",
        "VND" to "\u20AB", // ₫
        "EUR" to "\u20AC", // €
        "CNY" to "\u5143", // 元
        "KRW" to "\u20A9", // ₩
        "GBP" to "\u00A3", // £
        "JPY" to "\u5186", // 円
        "CHF" to "\u20A3", // ₣
        "SGD" to "$",
        "THB" to "\u0E3F" // ฿
    )

    private fun convertCurrency(amount: Float, from: String, to: String): Float {
        val rateFrom = exchangeRates[from] ?: 1f
        val rateTo = exchangeRates[to] ?: 1f
        return amount / rateFrom * rateTo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var currentEditText: EditText? = null

        val inputCurrency = findViewById<EditText>(R.id.input_currency)
        val outputCurrency = findViewById<EditText>(R.id.output_currency)
        val spinnerFrom: Spinner = findViewById(R.id.spinner_from)
        val spinnerTo: Spinner = findViewById(R.id.spinner_to)

        val currencyList = exchangeRates.keys.map { "$it (${currencySymbols[it] ?: ""})" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        spinnerFrom.setSelection(currencyList.indexOfFirst { it.startsWith("USD") })
        spinnerTo.setSelection(currencyList.indexOfFirst { it.startsWith("VND") })

        val buttonIds = intArrayOf(
            R.id.button_number_0, R.id.button_number_1, R.id.button_number_2,
            R.id.button_number_3, R.id.button_number_4, R.id.button_number_5,
            R.id.button_number_6, R.id.button_number_7, R.id.button_number_8, R.id.button_number_9
        )
        for (id in buttonIds) {
            val btn = findViewById<Button>(id)
            btn.setOnClickListener { view ->
                val number = (view as Button).text.toString()
                currentEditText?.apply {
                    val current = text.toString()
                    setText(if (current == "0") number else current + number)
                }
            }
        }

        findViewById<Button>(R.id.button_delete).setOnClickListener {
            currentEditText?.apply {
                val current = text.toString()
                setText(if (current.length > 1) current.dropLast(1) else "0")
            }
        }

        findViewById<Button>(R.id.button_delete_all).setOnClickListener {
            currentEditText?.setText("0")
        }

        inputCurrency.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) currentEditText = inputCurrency
        }

        outputCurrency.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) currentEditText = outputCurrency
        }

        inputCurrency.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (inputCurrency.hasFocus()) {
                    val inputAmount = s.toString().toFloatOrNull() ?: 0f
                    val from = spinnerFrom.selectedItem.toString().substringBefore(" ")
                    val to = spinnerTo.selectedItem.toString().substringBefore(" ")
                    val result = convertCurrency(inputAmount, from, to)
                    Log.d("ConvertInput", "$inputAmount $from -> $result $to")
                    outputCurrency.setText(String.format("%.2f", result))
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        outputCurrency.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (outputCurrency.hasFocus()) {
                    val outputAmount = s.toString().toFloatOrNull() ?: 0f
                    val from = spinnerTo.selectedItem.toString().substringBefore(" ")
                    val to = spinnerFrom.selectedItem.toString().substringBefore(" ")
                    val result = convertCurrency(outputAmount, from, to)
                    Log.d("ConvertOutput", "$outputAmount $from -> $result $to")
                    inputCurrency.setText(String.format("%.2f", result))
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }
}