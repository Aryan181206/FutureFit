package com.example.futurefit.ProfileActivity

import android.os.Bundle
import android.widget.TextView
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
        val skillsToLearn: List<String> = emptyList(),
        val predictionKey: String = "",
        val firestorePath: String = ""
    )

    private lateinit var recyclerView: RecyclerView
    internal lateinit var adapter: CareerAdapter
    internal val careerList = mutableListOf<PredictedCareer>()

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
        adapter = CareerAdapter(careerList) // No long press callback for delete
        recyclerView.adapter = adapter

        fetchCareerPredictions()

        val deleteButton = findViewById<TextView>(R.id.deleteallprediction)
        deleteButton.setOnClickListener {
            deleteCareerPredictions()
        }
    }

    private fun fetchCareerPredictions() {
        val db = FirebaseFirestore.getInstance()
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        db.collection("Users")
            .document(userEmail)
            .collection("PredictionData")
            .document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    careerList.clear()
                    for ((predictionKeyRaw, predictionValueRaw) in document.data ?: emptyMap()) {
                        val predictionKey = predictionKeyRaw as? String ?: continue
                        val predictionValue = predictionValueRaw as? Map<*, *> ?: continue

                        if (predictionKey.startsWith("Prediction")) {
                            for ((careerKeyRaw, careerValueRaw) in predictionValue) {
                                val careerKey = careerKeyRaw as? String ?: continue
                                val careerValue = careerValueRaw as? Map<*, *> ?: continue

                                if (careerKey.startsWith("Career_")) {
                                    val path = "Users/$userEmail/PredictionData/$userEmail"
                                    val predictedCareer = PredictedCareer(
                                        careerName = careerValue["Career_Name"] as? String ?: "",
                                        matchPercentage = (careerValue["Match_Percentage"] as? Long)?.toInt() ?: 0,
                                        reasonFit = careerValue["Reason_Fit"] as? String ?: "",
                                        recommendedCourses = careerValue["Recommended_courses"] as? List<String> ?: emptyList(),
                                        skillsToLearn = careerValue["Skills_to_learn"] as? List<String> ?: emptyList(),
                                        predictionKey = "$predictionKey.$careerKey",
                                        firestorePath = "$path/$predictionKey/$careerKey"
                                    )
                                    careerList.add(predictedCareer)
                                }
                            }
                        }
                    }

                    careerList.sortByDescending { it.matchPercentage }
                    adapter.notifyDataSetChanged()

                    if (careerList.isEmpty()) {
                        Toast.makeText(this, "No predictions found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No prediction data found", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load predictions", Toast.LENGTH_SHORT).show()
            }
    }
}

private fun AllPredictedCareer.deleteCareerPredictions() {
    val db = FirebaseFirestore.getInstance()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

    // Show confirmation dialog
    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
    builder.setTitle("Delete All Predictions")
    builder.setMessage("Are you sure you want to delete all predicted careers? This action cannot be undone.")

    builder.setPositiveButton("Yes") { dialog, _ ->
        // Path to the prediction document
        val predictionDocRef = db.collection("Users")
            .document(userEmail)
            .collection("PredictionData")
            .document(userEmail)

        // Delete the document
        predictionDocRef.delete()
            .addOnSuccessListener {
                // Clear the RecyclerView list and refresh
                careerList.clear()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "All career predictions deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete predictions: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        dialog.dismiss()
    }

    builder.setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
    }

    val dialog = builder.create()
    dialog.show()
}

