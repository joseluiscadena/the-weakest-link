package com.josecadena.theweakestlink

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.bank_amount_text_view
import kotlinx.android.synthetic.main.activity_main.bank_button
import kotlinx.android.synthetic.main.activity_main.radio0
import kotlinx.android.synthetic.main.activity_main.radio_group
import kotlinx.android.synthetic.main.activity_main.restore_button
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    lateinit var radio: RadioButton
    lateinit var results: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        radio_group.setOnCheckedChangeListener { _, checkedId ->
            radio = findViewById(checkedId)
            with(sharedPref.edit()) {
                putInt(SELECTED_RADIO_ID_KEY, radio.id)
                apply()
            }
        }

        bank_button.setOnClickListener {
            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            val total = bank_amount_text_view.tag.toString().toInt() + radio.tag.toString().toInt()
            setBankAmountTextViewState(total.toString(), format.format(total).substringBefore("."))
            radio_group.check(R.id.radio0)
        }

        restore_button.setOnClickListener {
            radio_group.check(R.id.radio0)
            radio = radio0
            with(sharedPref.edit()) {
                putString(RESULTS_KEY, results.plus(bank_amount_text_view.text.toString()).plus(","))
                apply()
            }
            setBankAmountTextViewState("0", getString(R.string.zero_value))
        }
    }

    private fun setBankAmountTextViewState(tag: String, text: String) {
        bank_amount_text_view.tag = tag
        bank_amount_text_view.text = text
    }

    override fun onResume() {
        super.onResume()
        restoreStateFromPreferences()
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle(getString(R.string.results))
            setMessage(results)
            setPositiveButton(getString(R.string.accept), null)
            show()
        }
    }

    private fun restoreStateFromPreferences() {
        sharedPref = getPreferences(MODE_PRIVATE)
        results = sharedPref.getString(RESULTS_KEY, "").toString()
        setBankAmountTextViewState(
            sharedPref.getString(BANK_AMOUNT_TAG_KEY, "0").toString(),
            sharedPref.getString(BANK_AMOUNT_TEXT_KEY, "$0").toString()
        )
        val selectedRadioId = sharedPref.getInt(SELECTED_RADIO_ID_KEY, R.id.radio0)
        radio_group.check(selectedRadioId)
        radio = findViewById(selectedRadioId)
    }

    override fun onPause() {
        super.onPause()
        with(sharedPref.edit()) {
            putString(BANK_AMOUNT_TAG_KEY, bank_amount_text_view.tag.toString())
            putString(BANK_AMOUNT_TEXT_KEY, bank_amount_text_view.text.toString())
            apply()
        }
    }

    companion object {
        const val SELECTED_RADIO_ID_KEY = "selected_radio_id"
        const val BANK_AMOUNT_TAG_KEY = "bank_amount_tag"
        const val BANK_AMOUNT_TEXT_KEY = "bank_amount_text"
        const val RESULTS_KEY = "results"
    }
}
