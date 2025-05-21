package com.example.futurefit.AssessmentResult

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.BottomBar
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AptitudeResult : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aptitude_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val numericalScore = findViewById<TextView>(R.id.numericalScore)
        val logicalScore = findViewById<TextView>(R.id.logicalScore)
        val verbalScore = findViewById<TextView>(R.id.verbalScore)

        val numprogress = findViewById<ProgressBar>(R.id.numprogress)
        val logprogress = findViewById<ProgressBar>(R.id.logprogress)
        val vebprogress = findViewById<ProgressBar>(R.id.vebprogress)

        val backhome = findViewById<CardView>(R.id.backhome)
        backhome.setOnClickListener {
            startActivity(Intent(this, BottomBar::class.java))
            finish()
        }

        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.email != null) {
            val email = currentUser.email!!
            db.collection("Users").document(email).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val logicalStr = document.getString("Aptitude_Logical_Score")?.replace("%", "") ?: "0.0"
                        val numericalStr = document.getString("Aptitude_Numerical_Score")?.replace("%", "") ?: "0.0"
                        val verbalStr = document.getString("Aptitude_Verbal_Score")?.replace("%", "") ?: "0.0"

                        val logical = logicalStr.toFloatOrNull()?.toInt() ?: 0
                        val numerical = numericalStr.toFloatOrNull()?.toInt() ?: 0
                        val verbal = verbalStr.toFloatOrNull()?.toInt() ?: 0

                        logicalScore.text = "$logical%"
                        numericalScore.text = "$numerical%"
                        verbalScore.text = "$verbal%"

                        logprogress.progress = logical
                        numprogress.progress = numerical
                        vebprogress.progress = verbal
                    } else {
                        Toast.makeText(this, "User document not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching data: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not signed in or email missing.", Toast.LENGTH_SHORT).show()
        }
    }
}
