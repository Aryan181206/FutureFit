package com.example.futurefit.Assessment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class AptitudeLogicalQuiz : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val totalTimeInMillis: Long = 5 * 60 * 1000  // 5 minutes

    private lateinit var questionText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var nextButton: MaterialButton

    private lateinit var progressBar: ProgressBar

    private var currentQuestionIndex = 0
    private var score = 0
    private var questions: List<Question> = listOf()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    data class Question(
        val id: Int,
        val text: String,
        val options: List<String>,
        val correctAnswer: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aptitude_logical_quiz)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        timerText = findViewById(R.id.timerText)
        questionText = findViewById(R.id.questionText)
        optionsGroup = findViewById(R.id.optionsGroup)
        nextButton = findViewById(R.id.nextButton)

        progressBar = findViewById(R.id.progressBar2)

        startTimer()
        loadQuestions()
        setupProgressBar()
        displayQuestion()

        nextButton.setOnClickListener {
            val selectedOptionId = optionsGroup.checkedRadioButtonId
            if (selectedOptionId == -1) {
                Toast.makeText(this, "You can't skip", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadioButton: RadioButton = findViewById(selectedOptionId)
            val selectedAnswer = selectedRadioButton.text.toString()

            if (selectedAnswer == questions[currentQuestionIndex].correctAnswer) {
                score++
            }

            currentQuestionIndex++

            if (currentQuestionIndex < questions.size) {
                updateProgressBar()
                displayQuestion()
            } else {
                finishQuiz()
            }
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                timerText.text = "Time Left: $timeFormatted"
            }

            override fun onFinish() {
                Toast.makeText(this@AptitudeLogicalQuiz, "Time's up!", Toast.LENGTH_SHORT).show()
                finishQuiz()
            }
        }.start()
    }

    private fun loadQuestions() {
        val json = assets.open("Aptitude_LogicalQuestions.json")
        val reader = InputStreamReader(json)
        val gson = Gson()
        val questionListType = object : TypeToken<List<Question>>() {}.type
        questions = gson.fromJson(reader, questionListType)
    }

    private fun setupProgressBar() {
        progressBar.max = questions.size
        progressBar.progress = 1
    }

    private fun updateProgressBar() {
        progressBar.progress = currentQuestionIndex + 1
    }

    private fun displayQuestion() {
        val question = questions[currentQuestionIndex]
        questionText.text = question.text
        optionsGroup.clearCheck()

        val option1: RadioButton = findViewById(R.id.option1)
        val option2: RadioButton = findViewById(R.id.option2)
        val option3: RadioButton = findViewById(R.id.option3)
        val option4: RadioButton = findViewById(R.id.option4)

        option1.text = question.options[0]
        option2.text = question.options[1]
        option3.text = question.options[2]
        option4.text = question.options[3]
    }

    private fun finishQuiz() {
        val percentage = (score.toDouble() / questions.size) * 100
        val formattedPercentage = String.format("%.2f", percentage)
        val resultText = "Final Score: $score/${questions.size} ($formattedPercentage%)"

        Toast.makeText(this, "Logical Quiz Finished!", Toast.LENGTH_SHORT).show()

        countDownTimer.cancel()

        nextButton.isEnabled = false
        optionsGroup.clearCheck()
        for (i in 0 until optionsGroup.childCount) {
            optionsGroup.getChildAt(i).isEnabled = false
        }

        // Save to Firebase
        saveResultToFirestore(formattedPercentage)
    }

    private fun saveResultToFirestore(scorePercent: String) {
        val user = auth.currentUser
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPref.getString("email", null)

        if (user != null) {
            val docRef = firestore.collection("Users").document(savedEmail.toString())

            val data = mapOf(
                "Aptitude_Logical_Score" to "$scorePercent%"
            )

            docRef.set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener {
                    goToResultActivity("$scorePercent%")
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save result: ${it.message}", Toast.LENGTH_SHORT).show()
                    goToResultActivity("$scorePercent%")
                }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            goToResultActivity("$scorePercent%")
        }
    }

    private fun goToResultActivity(result: String) {
        val intent = Intent(this, AptitudeNumericalQuiz::class.java)
        startActivity(intent)
        finish()
    }
}
