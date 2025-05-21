package com.example.futurefit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_status)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Firestore & Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Views
        progressBar = findViewById(R.id.progressBar)
        val checkage = findViewById<ImageView>(R.id.agecheck)
        val checkqualification = findViewById<ImageView>(R.id.qualificationcheck)
        val checklocation = findViewById<ImageView>(R.id.locationcheck)
        val checkstream = findViewById<ImageView>(R.id.streamcheck)
        val checkcgpa = findViewById<ImageView>(R.id.cgpacheck)
        val checkname = findViewById<ImageView>(R.id.namecheck)
        val checkaptitude = findViewById<ImageView>(R.id.aptitudecheck)
        val checkpersonality = findViewById<ImageView>(R.id.personalitycheck)
        val checktechnical = findViewById<ImageView>(R.id.technicalcheck)
        val checksoft = findViewById<ImageView>(R.id.softcheck)
        val checkinterest = findViewById<ImageView>(R.id.interestcheck)
        val checkexperience = findViewById<ImageView>(R.id.experiencecheck)
        val checkcertification = findViewById<ImageView>(R.id.certificationcheck)
        val predicit = findViewById<CardView>(R.id.predicit)

        predicit.setOnClickListener {
            startActivity(Intent(this, PredicitionSplashScreen::class.java))
        }

        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.email != null) {
            val email = currentUser.email!!

            progressBar.visibility = View.VISIBLE // Show progress bar

            db.collection("Users").document(email).get()
                .addOnSuccessListener { document ->
                    progressBar.visibility = View.GONE // Hide on success

                    if (document != null && document.exists()) {
                        setStatusIcon(checkname, document.getString("Name"))
                        setStatusIcon(checkaptitude, document.getString("Aptitude_Logical_Score"))
                        setStatusIcon(checkpersonality, document.getString("Personality"))
                        setStatusIcon(checktechnical, document.get("TechnicalSkills"))
                        setStatusIcon(checksoft, document.get("SoftSkills"))
                        setStatusIcon(checkinterest, document.get("Interest"))
                        setStatusIcon(checkexperience, document.get("Experience"))
                        setStatusIcon(checkcgpa, document.get("CGPA"))
                        setStatusIcon(checkstream, document.get("Stream"))
                        setStatusIcon(checklocation, document.get("Location"))
                        setStatusIcon(checkqualification, document.get("Qualification"))
                        setStatusIcon(checklocation, document.get("Location"))
                        setStatusIcon(checkage, document.get("DateOfBirth"))
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE // Hide on failure
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setStatusIcon(view: ImageView, fieldValue: Any?) {
        val isValid = when (fieldValue) {
            is String -> fieldValue.isNotBlank()
            is List<*> -> fieldValue.isNotEmpty()
            is Map<*, *> -> fieldValue.isNotEmpty()
            else -> fieldValue != null
        }
        view.setImageResource(if (isValid) R.drawable.baseline_check_box_24 else R.drawable.cross)
    }
}
