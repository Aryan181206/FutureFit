package com.example.futurefit.AssessmentSplashScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.futurefit.Assessment.AptitudeLogicalQuiz
import com.example.futurefit.Assessment.InterestSurveyQuiz
import com.example.futurefit.R

class InterestSplashScreen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_interest_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backgroundGif: ImageView = findViewById(R.id.backgroundGif)
        Glide.with(this)
            .asGif()
            .load(R.drawable.universalgif)
            .into(backgroundGif)



        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, InterestSurveyQuiz::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

}