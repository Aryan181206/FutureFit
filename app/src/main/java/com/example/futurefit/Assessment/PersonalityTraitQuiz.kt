package com.example.futurefit.Assessment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.AssessmentResult.PersonalityResult
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.InputStream

class PersonalityTraitQuiz : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var dimensionTitle: TextView
    private lateinit var questionText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var nextButton: Button

    private val questionsList = mutableListOf<QuestionItem>()
    private var currentIndex = 0
    private val answersMap = mutableMapOf<String, MutableList<String>>()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    data class QuestionItem(
        val dimensionName: String,
        val dimensionKey: String,
        val question: String,
        val options: Map<String, String>
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personality_trait_quiz)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        dimensionTitle = findViewById(R.id.dimensionTitle)
        questionText = findViewById(R.id.questionText)
        optionsGroup = findViewById(R.id.optionsGroup)
        nextButton = findViewById(R.id.nextButton)
        progressBar = findViewById(R.id.progressBar2)

        loadQuestionsFromJSON()
        progressBar.max = questionsList.size
        showQuestion()

        nextButton.setOnClickListener {
            handleNextButton()
        }
    }

    private fun loadQuestionsFromJSON() {
        val inputStream: InputStream = assets.open("personalityTestQuestion.json")
        val jsonStr = inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(jsonStr)
        val dimensions = json.getJSONArray("dimensions")

        for (i in 0 until dimensions.length()) {
            val dimension = dimensions.getJSONObject(i)
            val dimName = dimension.getString("name")
            val dimKey = dimension.getString("key")
            val questions = dimension.getJSONArray("questions")

            for (j in 0 until questions.length()) {
                val qObj = questions.getJSONObject(j)
                val question = qObj.getString("question")
                val optionsObj = qObj.getJSONObject("options")
                val options = mutableMapOf<String, String>()
                for (key in optionsObj.keys()) {
                    options[key] = optionsObj.getString(key)
                }
                questionsList.add(QuestionItem(dimName, dimKey, question, options))
            }
        }
    }

    private fun showQuestion() {
        val current = questionsList[currentIndex]
        dimensionTitle.text = current.dimensionName
        questionText.text = current.question
        optionsGroup.removeAllViews()

        for ((code, text) in current.options) {
            val radioButton = RadioButton(this)
            radioButton.text = text
            radioButton.tag = code
            radioButton.id = View.generateViewId()
            optionsGroup.addView(radioButton)
        }

        progressBar.progress = currentIndex + 1
    }

    private fun handleNextButton() {
        val selectedId   = optionsGroup.checkedRadioButtonId

        if (selectedId == -1) {
            Toast.makeText(this, "Please select an option before proceeding!", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRadioButton : RadioButton ? = findViewById<RadioButton>(selectedId)
        val selectedOption = selectedRadioButton?.tag?.toString()

        if (selectedOption.isNullOrEmpty()) {
            Toast.makeText(this, "No option selected!", Toast.LENGTH_SHORT).show()
            return
        }

        val currentQuestion = questionsList[currentIndex]
        answersMap.getOrPut(currentQuestion.dimensionKey) { mutableListOf() }.add(selectedOption)

        currentIndex++

        if (currentIndex < questionsList.size) {
            showQuestion()
        } else {
            val finalType = calculatePersonalityType()
            saveResultToFirestore(finalType)
        }
    }

    private fun calculatePersonalityType(): String {
        val typeLetters = mutableListOf<String>()
        val explanation = mutableListOf<String>()

        val dimensionMap = mapOf(
            "I" to "Introversion", "E" to "Extroversion",
            "S" to "Sensing", "N" to "Intuition",
            "T" to "Thinking", "F" to "Feeling",
            "J" to "Judging", "P" to "Perceiving"
        )

        for ((_, responses) in answersMap) {
            val counts = responses.groupingBy { it }.eachCount()
            val selected = counts.maxByOrNull { it.value }?.key ?: ""
            typeLetters.add(selected)
            explanation.add(dimensionMap[selected] ?: selected)
        }

        return "${typeLetters.joinToString("")} - ${explanation.joinToString(", ")}"
    }

    private fun saveResultToFirestore(result: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email ?: return
            val userDocRef = firestore.collection("Users").document(userEmail)

            userDocRef.update("Personality", result)
                .addOnSuccessListener {
                    goToResultActivity(result)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save result: ${it.message}", Toast.LENGTH_LONG).show()
                    goToResultActivity(result)
                }
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            goToResultActivity(result)
        }
    }

    private fun goToResultActivity(result: String) {
        val intent = Intent(this, PersonalityResult::class.java)
        intent.putExtra("personality_result", result)
        startActivity(intent)
        finish()
    }
}
