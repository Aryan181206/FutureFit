package com.example.futurefit.Prediction

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_predicition_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Firestore & Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        //work  of data done here

        val currentuser = auth.currentUser
        if (currentuser != null && currentuser.email != null) {
            val email = currentuser.email!!
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
                        technicalSkills = document.get("TechnicalSkills") as? List<String>
                            ?: emptyList(),
                        experience = document.get("Experience") as? List<Map<String, Any>>
                            ?: emptyList(),
                        aiPredictionResult = ""
                    )

                    Toast.makeText(
                        this,
                        "Data fetched for ${userProfile?.name}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Generate Prompt
                    userProfile?.let {
                        careerPredictionPrompt = generatePromptForGemini(it)
                        Toast.makeText(this, "Prompt generated", Toast.LENGTH_SHORT)
                            .show()    // for checking
                        Log.d("CareerPrompt", careerPredictionPrompt)

                        // Send Prompt to Gemini AI code for AI prediction
                        startPrediction(careerPredictionPrompt.toString())

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
                val outputText = response.text ?: "No response from model"

                userProfile = userProfile?.copy(aiPredictionResult = outputText)

                Toast.makeText(
                    this@PredicitionSplashScreen,
                    "Out put generated",
                    Toast.LENGTH_SHORT
                ).show()

                saveJsonToFile(outputText)

            } catch (e: Exception) {
                Toast.makeText(
                    this@PredicitionSplashScreen,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@PredicitionSplashScreen,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveJsonToFile(jsonContent: String) {
        try {
            val fileName = "carrer_prediction_result.json"
            val file = File(filesDir, fileName)
            FileOutputStream(file).use { output ->
                output.write(jsonContent.toByteArray())
                output.flush()
            }
            Toast.makeText(this, "JSON saved to $fileName", Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Toast.makeText(this, "Failed to save Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}