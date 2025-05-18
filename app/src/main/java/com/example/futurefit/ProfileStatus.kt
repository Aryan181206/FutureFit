package com.example.futurefit

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.Prediction.PredicitionSplashScreen
import java.nio.file.Files.find

class ProfileStatus : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_status)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //for tick and wrong directly search to firebase firestore


        //initialisation
        val checkname = findViewById<ImageView>(R.id.namecheck)
        val checkage = findViewById<ImageView>(R.id.agecheck)
        val checklocation = findViewById<ImageView>(R.id.locationcheck)
        val checkstream = findViewById<ImageView>(R.id.streamcheck)
        val checkqualification = findViewById<ImageView>(R.id.qualificationcheck)
        val checkcgpa = findViewById<ImageView>(R.id.cgpacheck)
        val checkaptitude = findViewById<ImageView>(R.id.aptitudecheck)
        val checkpersonality = findViewById<ImageView>(R.id.personalitycheck)
        val checktechnical = findViewById<ImageView>(R.id.technicalcheck)
        val checksoft = findViewById<ImageView>(R.id.softcheck)
        val checkinterest = findViewById<ImageView>(R.id.interestcheck)
        val checkexperience = findViewById<ImageView>(R.id.experiencecheck)
        val checkcertification = findViewById<ImageView>(R.id.certificationcheck)
        val predicit = findViewById<CardView>(R.id.predicit)

        predicit.setOnClickListener {
            checkfromfirebase()
            startActivity(Intent(this, PredicitionSplashScreen ::class.java))
        }

    }
}