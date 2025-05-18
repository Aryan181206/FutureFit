package com.example.futurefit.AssessmentResult

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.BottomBar
import com.example.futurefit.R

class AptitudeResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aptitude_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backhome = findViewById<androidx.cardview.widget.CardView>(R.id.backhome)
        backhome.setOnClickListener {
            startActivity(Intent(this, BottomBar::class.java))
            finish()
        }
        val numericalScore = findViewById<TextView>(R.id.numericalScore)
        val logicalScore = findViewById<TextView>(R.id.logicalScore)
        val verbalScore = findViewById<TextView>(R.id.verbalScore)

        val numprogress = findViewById<ProgressBar>(R.id.numprogress)
        val logprogress = findViewById<ProgressBar>(R.id.logprogress)
        val vebprogress = findViewById<ProgressBar>(R.id.vebprogress)






    }
}