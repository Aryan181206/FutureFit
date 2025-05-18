package com.example.futurefit.AssessmentResult

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.BottomBar
import com.example.futurefit.R

class PersonalityResult : AppCompatActivity() {


    private lateinit var mbtiResult: TextView
    private lateinit var personalityDesc: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personality_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mbtiResult = findViewById(R.id.mbtiResult)
        personalityDesc = findViewById(R.id.personalityDesc)

        val result = intent.getStringExtra("personality_result") ?: "Unknown"
        val typeCode = result.substring(0, 4)


        mbtiResult.text = typeCode
        personalityDesc.text = getDescriptionForType(typeCode)

        val backButton = findViewById<CardView>(R.id.backhome)
        backButton.setOnClickListener {
            startActivity(Intent(this@PersonalityResult, BottomBar::class.java))
            finish()
        }

    }
    private fun getDescriptionForType(type: String): String {
        return when (type) {
            "INTJ" -> "INTJs are strategic thinkers who value logic, structure, and long-term planning."
            "INTP" -> "INTPs are innovative, analytical, and love exploring abstract theories and ideas."
            "ENTJ" -> "ENTJs are natural leaders who thrive in planning, organizing, and executing strategies."
            "ENTP" -> "ENTPs are energetic, curious, and thrive in fast-paced, innovative environments."

            "INFJ" -> "INFJs are insightful, empathetic, and driven by deep personal values and ideals."
            "INFP" -> "INFPs are idealistic, deeply caring, and guided by strong inner values."
            "ENFJ" -> "ENFJs are charismatic leaders, excellent at inspiring and helping others grow."
            "ENFP" -> "ENFPs are enthusiastic, creative, and full of energy and imagination."

            "ISTJ" -> "ISTJs are responsible, practical, and highly reliable with a strong sense of duty."
            "ISFJ" -> "ISFJs are loyal helpers, attentive to others’ needs and highly responsible."
            "ESTJ" -> "ESTJs are organized, assertive, and natural at managing people and projects."
            "ESFJ" -> "ESFJs are caring, social, and value harmony and cooperation in groups."

            "ISTP" -> "ISTPs are action-oriented problem solvers who enjoy working with their hands and tools."
            "ISFP" -> "ISFPs are gentle, sensitive, and enjoy living in the moment with a strong aesthetic sense."
            "ESTP" -> "ESTPs are energetic, perceptive, and love taking risks and being in the center of action."
            "ESFP" -> "ESFPs are spontaneous, playful, and enjoy entertaining others and experiencing life vividly."

            else -> "Unique personality type! Reflect on your strengths and preferences."
        }
    }
}