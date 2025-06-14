package com.example.futurefit.AssessmentResult

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import com.example.futurefit.BottomBar
import com.example.futurefit.R

class InterestResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_result)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val resultTextView = findViewById<TextView>(R.id.interestResult)

        val result = intent.getStringExtra("RIASEC_RESULT") ?: "No result available"
        resultTextView.text = result

        val backButton = findViewById<CardView>(R.id.backhome)
        backButton.setOnClickListener {
            startActivity(Intent(this, BottomBar::class.java))
            finish()
        }
    }
}
