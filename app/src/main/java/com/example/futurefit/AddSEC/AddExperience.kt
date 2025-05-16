package com.example.futurefit.AddSEC

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddExperience : AppCompatActivity() {

    private lateinit var employmentContainer: LinearLayout
    private lateinit var addPositionButton: CardView
    private lateinit var saveButton: CardView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_experience)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        employmentContainer = findViewById(R.id.positionContainer)
        addPositionButton = findViewById(R.id.addPositionCard)
        saveButton = findViewById(R.id.saveChanges)

        addPositionButton.setOnClickListener {
            addNewEmploymentCard()
        }

        saveButton.setOnClickListener {
            saveEmploymentHistoryToFirebase()
        }



    }
    private fun addNewEmploymentCard() {
        val cardView = layoutInflater.inflate(R.layout.item_addposition, null)



        val removeButton = cardView.findViewById<Button>(R.id.btnRemove)
        removeButton.setOnClickListener {
            employmentContainer.removeView(cardView)
        }

        employmentContainer.addView(cardView)
    }

    private fun saveEmploymentHistoryToFirebase() {
        val employmentList = mutableListOf<Map<String, Any>>()

        for (i in 0 until employmentContainer.childCount) {
            val cardView = employmentContainer.getChildAt(i)







            val position = cardView.findViewById<EditText>(R.id.etJobTitle).text.toString().trim()
            val company = cardView.findViewById<EditText>(R.id.etCompanyName).text.toString().trim()
            val location = cardView.findViewById<EditText>(R.id.etLocation).text.toString().trim()
            val experienceStr = cardView.findViewById<EditText>(R.id.etYearsOfExperience).text.toString().trim()

            val experienceYears = experienceStr.toIntOrNull() ?: 0

            if (position.isEmpty() || company.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill all fields in card $i", Toast.LENGTH_SHORT).show()
                return
            }


            val employment = mapOf(
                "Position" to position,
                "Company" to company,
                "Location" to location,
                "ExperienceYears" to experienceYears
            )

            employmentList.add(employment)
        }

        val email = auth.currentUser?.email

        firestore.collection("Users").document(email.toString())
            .update("Experience", employmentList)
            .addOnSuccessListener {
                Toast.makeText(this, "Experience Saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}