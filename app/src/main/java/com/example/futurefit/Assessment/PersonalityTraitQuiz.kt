package com.example.futurefit.Assessment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.AssessmentResult.PersonalityResult
import com.example.futurefit.R
import org.json.JSONObject
import java.io.InputStream
import kotlin.collections.iterator

class PersonalityTraitQuiz : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var dimensionTitle: TextView
    private lateinit var questionText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var nextButton: Button

    private val questionsList = mutableListOf<QuestionItem>()
    private var currentIndex = 0
    private val answersMap = mutableMapOf<String, MutableList<String>>() // dimensionKey -> [E, I, ...]

    data class QuestionItem(
        val dimensionName: String,
        val dimensionKey: String,
        val question: String,
        val options: Map<String, String>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personality_trait_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize views
       // dimensionTitle = findViewById(R.id.dimensionTitle)
        questionText = findViewById(R.id.questionText)
        optionsGroup = findViewById(R.id.optionsGroup)
        nextButton = findViewById(R.id.nextButton)
        progressBar = findViewById(R.id.progressBar2)

        loadQuestionsFromJSON()
        progressBar.max = questionsList.size
        showQuestion()

        nextButton.setOnClickListener {
            val selectedId = optionsGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "You can't skip this question!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedOption = findViewById<RadioButton>(selectedId).tag.toString()
            val currentQuestion = questionsList[currentIndex]
            answersMap.getOrPut(currentQuestion.dimensionKey) { mutableListOf() }.add(selectedOption)

            currentIndex++
            if (currentIndex < questionsList.size) {
                showQuestion()
            } else {
                val finalType = calculatePersonalityType()
                Toast.makeText(this, "Test finished!", Toast.LENGTH_SHORT).show()
                goToResultActivity(finalType)
            }
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
            optionsGroup.addView(radioButton)
        }

        progressBar.progress = currentIndex
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

        for ((dimension, responses) in answersMap) {
            val counts = responses.groupingBy { it }.eachCount()
            val selected = counts.maxByOrNull { it.value }?.key ?: ""
            typeLetters.add(selected)
            explanation.add(dimensionMap[selected] ?: selected)
        }
        return "${typeLetters.joinToString("")} - ${explanation.joinToString(", ")}"
    }

    private fun goToResultActivity(result: String) {
        val intent = Intent(this, PersonalityResult::class.java)
        intent.putExtra("personality_result", result)
        startActivity(intent)
        finish()
    }
}