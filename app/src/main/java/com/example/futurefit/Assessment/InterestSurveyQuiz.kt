package com.example.futurefit.Assessment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.AssessmentResult.InterestResult
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.json.JSONObject
import java.io.InputStream

class InterestSurveyQuiz : AppCompatActivity() {

    private lateinit var dimensionTitle: TextView
    private lateinit var questionText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var nextButton: Button
    private lateinit var progressBar: ProgressBar

    private val questions = mutableListOf<QuestionItem>()
    private var currentQuestionIndex = 0
    private val scoresMap = mutableMapOf<String, Int>()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_survey_quiz)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        dimensionTitle = findViewById(R.id.dimensionTitle)
        questionText = findViewById(R.id.questionText)
        optionsGroup = findViewById(R.id.optionsGroup)
        nextButton = findViewById(R.id.nextButton)
        progressBar = findViewById(R.id.progressBar2)

        loadQuestionsFromJSON()
        progressBar.max = questions.size
        displayQuestion()

        nextButton.setOnClickListener {
            val selectedId = optionsGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadio = findViewById<RadioButton>(selectedId)
            val selectedScore = selectedRadio.tag.toString().toInt()
            val current = questions[currentQuestionIndex]

            scoresMap[current.dimensionKey] =
                scoresMap.getOrDefault(current.dimensionKey, 0) + selectedScore

            currentQuestionIndex++
            if (currentQuestionIndex < questions.size) {
                displayQuestion()
            } else {
                val result = calculateTopRIASEC()
                saveResultToFirestore(result)
            }
        }
    }

    private fun loadQuestionsFromJSON() {
        val inputStream: InputStream = assets.open("riasec_survey.json")
        val jsonStr = inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(jsonStr)
        val dimensions = json.getJSONArray("dimensions")

        for (i in 0 until dimensions.length()) {
            val dimension = dimensions.getJSONObject(i)
            val dimName = dimension.getString("name")
            val dimKey = dimension.getString("key")
            val questionsArray = dimension.getJSONArray("questions")

            for (j in 0 until questionsArray.length()) {
                val questionObj = questionsArray.getJSONObject(j)
                val questionText = questionObj.getString("question")
                val optionsJSON = questionObj.getJSONObject("options")
                val optionsMap = mutableMapOf<String, String>()

                for (key in optionsJSON.keys()) {
                    optionsMap[key] = optionsJSON.getString(key)
                }

                questions.add(QuestionItem(dimName, dimKey, questionText, optionsMap))
            }
        }
    }

    private fun displayQuestion() {
        val question = questions[currentQuestionIndex]
        dimensionTitle.text = question.dimensionName
        questionText.text = question.question
        optionsGroup.removeAllViews()

        // Clear previous selection explicitly
        optionsGroup.clearCheck()

        for ((score, text) in question.options) {
            val radio = RadioButton(this).apply {
                this.text = text
                this.tag = score
                this.id = RadioButton.generateViewId()
            }
            optionsGroup.addView(radio)
        }

        progressBar.progress = currentQuestionIndex + 1
    }

    private fun calculateTopRIASEC(): String {
        val sorted = scoresMap.entries.sortedByDescending { it.value }.take(3)

        val dimensionFullNames = mapOf(
            "R" to "Realistic",
            "I" to "Investigative",
            "A" to "Artistic",
            "S" to "Social",
            "E" to "Enterprising",
            "C" to "Conventional"
        )

        return sorted.joinToString(", ") { dimensionFullNames[it.key] ?: it.key }
    }

    private fun saveResultToFirestore(result: String) {
        val user = auth.currentUser
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPref.getString("email", null)

        if (user != null && savedEmail != null) {
            val userDocRef = firestore.collection("Users").document(savedEmail)
            val data = mapOf("Interest" to result)

            userDocRef.set(data, SetOptions.merge())
                .addOnSuccessListener {
                    goToResultActivity(result)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save result: ${e.message}", Toast.LENGTH_LONG).show()
                    goToResultActivity(result)
                }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            goToResultActivity(result)
        }
    }

    private fun goToResultActivity(result: String) {
        val intent = Intent(this, InterestResult::class.java)
        intent.putExtra("RIASEC_RESULT", result)
        startActivity(intent)
        finish()
    }

    data class QuestionItem(
        val dimensionName: String,
        val dimensionKey: String,
        val question: String,
        val options: Map<String, String>
    )
}
