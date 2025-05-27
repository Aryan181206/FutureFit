package com.example.futurefit.Prediction

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.BottomBar
import com.example.futurefit.DataClass.Career
import com.example.futurefit.DataClass.CareerData
import com.example.futurefit.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.delay
import java.io.File

class PredicitionResult : AppCompatActivity() {


    // Store parsed career data globally to access it in button click listeners
    private var careerList: List<Career> = emptyList()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_predicition_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
            return
        }
        val email = user.email ?: return

        // Read career prediction result file
        val fileName = "career_prediction_result.json"
        val file = File(filesDir, fileName)
        if (!file.exists()) {
            Toast.makeText(this, "Prediction file not found!", Toast.LENGTH_LONG).show()
            return
        }

        val jsonString = file.readText()
        val gson = Gson()

        val careerData: CareerData? = try {
            gson.fromJson(jsonString, CareerData::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            Toast.makeText(this, "Error parsing prediction data", Toast.LENGTH_LONG).show()
            return
        }

        // Validate and extract career list
        if (careerData?.Career_Name.isNullOrEmpty()) {
            Toast.makeText(this, "No career prediction data found", Toast.LENGTH_LONG).show()

            showloadingtext(R.id.careerName1, R.id.reason1, R.id.skills1, R.id.courses1, R.id.match1,
                R.id.careerName2, R.id.reason2, R.id.skills2, R.id.courses2, R.id.match2,
                R.id.careerName3, R.id.reason3, R.id.skills3, R.id.courses3, R.id.match3)

            return
        }

        careerList = careerData!!.Career_Name
        // Save to Firestore
        savePredictionToFirestore(email, careerList)

        // Set up save button click listeners
        setSaveClickListeners(email)

        // Populate up to 3 cards
        if (careerList.size >= 3) {
            setCareerCard(careerList[0], R.id.careerName1, R.id.reason1, R.id.skills1, R.id.courses1, R.id.match1)
            setCareerCard(careerList[1], R.id.careerName2, R.id.reason2, R.id.skills2, R.id.courses2, R.id.match2)
            setCareerCard(careerList[2], R.id.careerName3, R.id.reason3, R.id.skills3, R.id.courses3, R.id.match3)

            // Set onClick for buttons on first card
            findViewById<Button>(R.id.skills1).setOnClickListener {
                val skillsText = careerList[0].Skills_to_learn.joinToString("\n• ", prefix = "• ")
                showInfoDialog("Skills to Learn", skillsText , R.drawable.skillsicon)    // kaam baaki hai image wala

            }
            findViewById<Button>(R.id.courses1).setOnClickListener {
                val courseText = careerList[0].Recommended_courses.joinToString("\n• ", prefix = "• ")
                showInfoDialog("Recommended Courses", courseText, R.drawable.recomdcourses)
            }
            // Set onClick for buttons on second card
            findViewById<Button>(R.id.skills2).setOnClickListener {
                val skillsText = careerList[1].Skills_to_learn.joinToString("\n• ", prefix = "• ")
                showInfoDialog("Skills to Learn", skillsText, R.drawable.skillsicon)
            }
            findViewById<Button>(R.id.courses2).setOnClickListener {
                val courseText = careerList[1].Recommended_courses.joinToString("\n• ", prefix = "• ")
                showInfoDialog("Recommended Courses", courseText, R.drawable.recomdcourses)
            }

            // Set onClick for buttons on third card
            findViewById<Button>(R.id.skills3).setOnClickListener {
                val skillsText = careerList[2].Skills_to_learn.joinToString("\n• ", prefix = "• ")
                showInfoDialog("Skills to Learn", skillsText, R.drawable.skillsicon)
            }
            findViewById<Button>(R.id.courses3).setOnClickListener {
                val courseText = careerList[2].Recommended_courses.joinToString("\n• ", prefix = "• ")
                showInfoDialog("Recommended Courses", courseText, R.drawable.recomdcourses)
            }


        } else {
            Toast.makeText(this, "Less than 3 career predictions available", Toast.LENGTH_SHORT).show()
        }

        val back = findViewById<MaterialButton>(R.id.backhome)
        back.setOnClickListener {
            startActivity(Intent(this, BottomBar ::class.java))
        }

    }

    private fun setSaveClickListeners(email: String) {
        val saveIcon1 = findViewById<ImageView>(R.id.save1)
        val saveIcon2 = findViewById<ImageView>(R.id.save2)
        val saveIcon3 = findViewById<ImageView>(R.id.save3)

        saveIcon1.setOnClickListener {
            if (careerList.size > 0) saveCareerToFirestore(email, careerList[0])
        }
        saveIcon2.setOnClickListener {
            if (careerList.size > 1) saveCareerToFirestore(email, careerList[1])
        }
        saveIcon3.setOnClickListener {
            if (careerList.size > 2) saveCareerToFirestore(email, careerList[2])
        }
    }


    private fun savePredictionToFirestore(email: String, careerList: List<Career>) {
        val rootRef = firestore.collection("Users").document(email)
            .collection("PredictionData").document(email)

        rootRef.get()
            .addOnSuccessListener { document ->
                val existingData = document.data ?: emptyMap<String, Any>()
                val predictionCount = existingData.keys.count { it.startsWith("Prediction") }
                val nextFieldName = "Prediction${predictionCount + 1}time"

                val predictionMap = mutableMapOf<String, Any>()

                for ((index, career) in careerList.withIndex()) {
                    val careerMap = mapOf(
                        "Career_Name" to career.Career_Name,
                        "Reason_Fit" to career.Reason_Fit,
                        "Skills_to_learn" to career.Skills_to_learn,
                        "Recommended_courses" to career.Recommended_courses,
                        "Match_Percentage" to career.Match_Percentage
                    )
                    predictionMap["Career_${index + 1}"] = careerMap
                }

                val updateMap = mapOf(nextFieldName to predictionMap)

                rootRef.update(updateMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Prediction saved as $nextFieldName", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // If document doesn't exist, create it
                        rootRef.set(updateMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Prediction saved as $nextFieldName", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { err ->
                                err.printStackTrace()
                                Toast.makeText(this, "Error saving prediction", Toast.LENGTH_LONG).show()
                            }
                    }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Error accessing Firestore", Toast.LENGTH_LONG).show()
            }
    }



    private fun showloadingtext(
        n1: Int, r1: Int, s1: Int, c1: Int, m1: Int,
        n2: Int, r2: Int, s2: Int, c2: Int, m2: Int,
        n3: Int, r3: Int, s3: Int, c3: Int, m3: Int
    ) {
        val ids = listOf(n1, r1, s1, c1, m1, n2, r2, s2, c2, m2, n3, r3, s3, c3, m3)
        ids.forEach { findViewById<TextView>(it).text = "Loading..." }

        Handler(Looper.getMainLooper()).postDelayed({
            Toast.makeText(this, "Prediction Error! Try Again", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, BottomBar::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }, 15000)
    }

    /**
     * Populates a single career card with data
     */
    private fun setCareerCard(
        career: Career,
        nameId: Int,
        reasonId: Int,
        skillsId: Int,
        coursesId: Int,
        matchId: Int
    ) {
        findViewById<TextView>(nameId).text = "Career: ${career.Career_Name}"
        findViewById<TextView>(reasonId).text = "Why it's a fit: ${career.Reason_Fit}"
        //findViewById<TextView>(skillsId).text = "Skills to Learn:\n- " + career.Skills_to_learn.joinToString("\n- ")
        //findViewById<TextView>(coursesId).text = "Recommended Courses:\n- " + career.Recommended_courses.joinToString("\n- ")
        findViewById<TextView>(matchId).text = "${career.Match_Percentage}%"
    }


    /**
     * Shows a custom dialog box with title and content
     */

    @SuppressLint("ResourceType")
    private fun showInfoDialog(title: String, content: String, img: Int,) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_info_popup, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle = dialogView.findViewById<TextView>(R.id.heading)
        val tvContent = dialogView.findViewById<TextView>(R.id.courseContent)
        val tvimageshow = dialogView.findViewById<ImageView>(R.id.imgshown)

        tvTitle.text = title
        tvContent.text = content
        tvimageshow.setImageResource(img)

        dialog.show()
    }

    private fun saveCareerToFirestore(email: String, career: Career) {
        val saveRef = firestore.collection("Users").document(email)
            .collection("SavedCareers").document(email)

        saveRef.get().addOnSuccessListener { document ->
            val existingData = document.data?.toMutableMap() ?: mutableMapOf()

            // Check if the career is already present by comparing Career_Name values
            val alreadyExists = existingData.values.any {
                it is Map<*, *> && it["Career_Name"] == career.Career_Name
            }

            if (alreadyExists) {
                Toast.makeText(this, "${career.Career_Name} already saved!", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            // Create a key like career1, career2, etc.
            val nextKey = "career${existingData.size + 1}"

            val careerMap = mapOf(
                "Career_Name" to career.Career_Name,
                "Reason_Fit" to career.Reason_Fit,
                "Skills_to_learn" to career.Skills_to_learn,
                "Recommended_courses" to career.Recommended_courses,
                "Match_Percentage" to career.Match_Percentage
            )

            existingData[nextKey] = careerMap

            // Save updated data
            saveRef.set(existingData)
                .addOnSuccessListener {
                    Toast.makeText(this, "${career.Career_Name} saved successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save ${career.Career_Name}", Toast.LENGTH_LONG).show()
                }

        }.addOnFailureListener {
            Toast.makeText(this, "Failed to retrieve saved careers", Toast.LENGTH_LONG).show()
        }
    }



}
