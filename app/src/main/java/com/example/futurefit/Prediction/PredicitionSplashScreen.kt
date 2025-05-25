package com.example.futurefit.Prediction

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.futurefit.DataClass.UserProfile
import com.example.futurefit.R
import com.example.futurefit.Util.generatePromptForGemini
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PredicitionSplashScreen : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var userProfile: UserProfile? = null
    private lateinit var careerPredictionPrompt: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_predicition_splash_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backgroundGif: ImageView = findViewById(R.id.gif)
        Glide.with(this)
            .asGif()
            .load(R.drawable.ailoading) // Your GIF in res/drawable
            .into(backgroundGif)

        // Firestore & Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.email != null) {
            val email = currentUser.email!!
            fetchUserData(email)
        }
    }

    private fun fetchUserData(email: String) {
        db.collection("Users").document(email).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    userProfile = UserProfile(
                        name = document.getString("Name") ?: "",
                        email = document.getString("Email") ?: "",
                        phone = document.getString("Phone") ?: "",
                        dateOfBirth = document.getString("DateOfBirth") ?: "",
                        gender = document.getString("Gender") ?: "",
                        location = document.getString("Location") ?: "",
                        stream = document.getString("Stream") ?: "",
                        qualification = document.getString("Qualification") ?: "",
                        cgpa = document.getString("CGPA") ?: "",
                        personality = document.getString("Personality") ?: "",
                        interest = document.getString("Interest") ?: "",
                        aptitudeLogical = document.getString("Aptitude_Logical_Score") ?: "",
                        aptitudeNumerical = document.getString("Aptitude_Numerical_Score") ?: "",
                        aptitudeVerbal = document.getString("Aptitude_Verbal_Score") ?: "",
                        softSkills = document.get("SoftSkills") as? List<String> ?: emptyList(),
                        technicalSkills = document.get("TechnicalSkills") as? List<String> ?: emptyList(),
                        physicalfitness = document.getString("Physical_Fitness") ?: "",
                        experience = document.get("Experience") as? List<Map<String, Any>> ?: emptyList(),
                        aiPredictionResult = ""
                    )

                    Toast.makeText(this, "Data fetched for ${userProfile?.name}", Toast.LENGTH_SHORT).show()

                    userProfile?.let {
                        careerPredictionPrompt = generatePromptForGemini(it)
                        Log.d("CareerPrompt", careerPredictionPrompt)
                        startPrediction(careerPredictionPrompt)
                    }

                } else {
                    Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun startPrediction(prompt: String) {
        if (prompt.isEmpty()) {
            Toast.makeText(this, "Prompt is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = "AIzaSyBCBWfKVi6hBieF3TDKTz7v6r7cnr7ipxU"
        )

        lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                var outputText = response.text ?: "No response from model"
                Log.d("RawOutput", outputText)

                // Clean the output text before saving
                val cleanedJson = extractValidJson(outputText)
                Log.d("CleanedJson", cleanedJson)

                userProfile = userProfile?.copy(aiPredictionResult = cleanedJson)
                saveJsonToFile(cleanedJson)

            } catch (e: Exception) {
                Toast.makeText(this@PredicitionSplashScreen, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("PredictionError", e.toString())
            }
        }
    }

    /**
     * Extract valid JSON content from a string, assuming it starts and ends somewhere in the middle.
     * Removes any text before the first '{' and after the last '}'.
     */
    private fun extractValidJson(text: String): String {
        val startIndex = text.indexOfFirst { it == '{' }
        val endIndex = text.indexOfLast { it == '}' }

        return if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            text.substring(startIndex, endIndex + 1).trim()
        } else {
            "{}" // fallback to empty JSON if boundaries are invalid
        }
    }

    private fun saveJsonToFile(jsonContent: String) {
        try {
            val fileName = "career_prediction_result.json"
            val file = File(filesDir, fileName)
            FileOutputStream(file).use { output ->
                output.write(jsonContent.toByteArray())
                output.flush()
            }
            Toast.makeText(this, "JSON saved to $fileName", Toast.LENGTH_SHORT).show()
            goNextActivity()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goNextActivity() {
        Toast.makeText(this, "Going to next activity", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, PredicitionResult::class.java))
        finish()
    }
}
