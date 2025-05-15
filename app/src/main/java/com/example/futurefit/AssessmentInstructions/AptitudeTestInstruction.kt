package com.example.futurefit.AssessmentInstructions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.AssessmentSplashScreen.AptitudeStartSplashScreen
import com.example.futurefit.R

class AptitudeTestInstruction : AppCompatActivity() {
    private lateinit var startTest : CardView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aptitude_test_instruction)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startTest = findViewById(R.id.startAptitudeTest)
        startTest.setOnClickListener {
            val intent = Intent(this@AptitudeTestInstruction, AptitudeStartSplashScreen :: class.java)
            startActivity(intent)
        }


    }
}