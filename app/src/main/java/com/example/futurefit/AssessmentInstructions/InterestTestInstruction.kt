package com.example.futurefit.AssessmentInstructions

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.AssessmentSplashScreen.InterestSplashScreen
import com.example.futurefit.R

class InterestTestInstruction : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_interest_test_instruction)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val startButton = findViewById<CardView>(R.id.startIntersetTest)
        startButton.setOnClickListener {
            startActivity((Intent(this@InterestTestInstruction, InterestSplashScreen::class.java)))

        }


    }
}