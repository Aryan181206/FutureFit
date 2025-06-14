package com.example.futurefit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.Prediction.PredicitionSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileStatus : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var predicit: CardView

    private val requiredFields = mapOf(
        "Name" to R.id.namecheck,
        "Aptitude_Logical_Score" to R.id.aptitudecheck,
        "Personality" to R.id.personalitycheck,
        "TechnicalSkills" to R.id.technicalcheck,
        "SoftSkills" to R.id.softcheck,
        "Interest" to R.id.interestcheck,
        "Experience" to R.id.experiencecheck,
        "CGPA" to R.id.cgpacheck,
        "Stream" to R.id.streamcheck,
        "Location" to R.id.locationcheck,
        "Qualification" to R.id.qualificationcheck,
        "DateOfBirth" to R.id.agecheck,
        "Physical_Fitness" to R.id.fitnesscheck // optional or same as "Personality"
    )

    private var isProfileComplete = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_status)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)
        predicit = findViewById(R.id.predicit)

        predicit.isEnabled = false
        predicit.setCardBackgroundColor(getColor(R.color.dark_gray))

        predicit.setOnClickListener {
            if (isProfileComplete) {
                startActivity(Intent(this, PredicitionSplashScreen::class.java))
                finish()
            } else {
                // Shake animation and toast
                val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
                predicit.startAnimation(shake)
                Toast.makeText(this, "Please complete your profile before proceeding.", Toast.LENGTH_SHORT).show()
            }
        }

        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.email != null) {
            val email = currentUser.email!!

            progressBar.visibility = View.VISIBLE

            db.collection("Users").document(email).get()
                .addOnSuccessListener { document ->
                    progressBar.visibility = View.GONE

                    if (document != null && document.exists()) {
                        var allValid = true

                        for ((field, iconId) in requiredFields) {
                            val value = document.get(field)
                            val isValid = isFieldValid(value)

                            val iconView = findViewById<ImageView>(iconId)
                            iconView.setImageResource(
                                if (isValid) R.drawable.baseline_check_box_24 else R.drawable.cross
                            )

                            if (!isValid) allValid = false
                        }

                        isProfileComplete = allValid
                        updatePredictionButton(allValid)

                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isFieldValid(fieldValue: Any?): Boolean {
        return when (fieldValue) {
            is String -> fieldValue.isNotBlank()
            is List<*> -> fieldValue.isNotEmpty()
            is Map<*, *> -> fieldValue.isNotEmpty()
            else -> fieldValue != null
        }
    }

    private fun updatePredictionButton(enable: Boolean) {
        predicit.isEnabled = enable
        predicit.setCardBackgroundColor(
            getColor(if (enable) R.color.Sky else R.color.dark_gray)
        )
    }
}
