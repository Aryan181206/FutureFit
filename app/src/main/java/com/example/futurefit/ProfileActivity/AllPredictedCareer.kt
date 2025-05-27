package com.example.futurefit.ProfileActivity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AllPredictedCareer : AppCompatActivity() {

    data class PredictedCareer(
        val careerName: String = "",
        val matchPercentage: Int = 0,
        val reasonFit: String = "",
        val recommendedCourses: List<String> = emptyList(),
        val skillsToLearn: List<String> = emptyList()
    )


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CareerAdapter
    private val careerList = mutableListOf<PredictedCareer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_predicted_career)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.allcareerRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CareerAdapter(careerList)
        recyclerView.adapter = adapter

        fetchCareerPredictions()
    }
    private fun fetchCareerPredictions() {
        val db = FirebaseFirestore.getInstance()
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("Users")
            .document(userEmail)
            .collection("PredictionData")
            .document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    careerList.clear()

                    // Loop through all prediction sets like Prediction1time, Prediction2time, etc.
                    for ((fieldKey, fieldValue) in document.data ?: emptyMap()) {
                        if (fieldKey.toString().startsWith("Prediction") && fieldValue is Map<*, *>) {
                            val predictionMap = fieldValue

                            for ((careerKey, careerValue) in predictionMap) {
                                if (careerKey.toString().startsWith("Career_") && careerValue is Map<*, *>) {
                                    val predictedCareer = PredictedCareer(
                                        careerName = careerValue["Career_Name"] as? String ?: "",
                                        matchPercentage = (careerValue["Match_Percentage"] as? Long)?.toInt() ?: 0,
                                        reasonFit = careerValue["Reason_Fit"] as? String ?: "",
                                        recommendedCourses = careerValue["Recommended_courses"] as? List<String> ?: emptyList(),
                                        skillsToLearn = careerValue["Skills_to_learn"] as? List<String> ?: emptyList()
                                    )
                                    careerList.add(predictedCareer)
                                }
                            }
                        }
                    }

                    // Sort by Match Percentage in descending order
                    careerList.sortByDescending { it.matchPercentage }

                    adapter.notifyDataSetChanged()

                    if (careerList.isEmpty()) {
                        Toast.makeText(this, "No career predictions found", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Career predictions loaded", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No prediction data found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load prediction data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}



