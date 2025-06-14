package com.example.futurefit.AssessmentInstructions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.Assessment.PersonalityTraitQuiz
import com.example.futurefit.AssessmentSplashScreen.PersonalityStartSplashScreen
import com.example.futurefit.R

class PersonalityTestInstruction : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personality_test_instruction)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val startPersonalityTestButton = findViewById<CardView>(R.id.startPersonalityTest)

        startPersonalityTestButton.setOnClickListener {
            startActivity(Intent(this, PersonalityStartSplashScreen::class.java))
            finish()
        }
    }
}