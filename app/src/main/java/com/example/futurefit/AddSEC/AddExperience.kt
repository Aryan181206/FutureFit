package com.example.futurefit.AddSEC

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
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

    private lateinit var pastexperience: CheckBox

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
        pastexperience = findViewById<CheckBox>(R.id.pastexpno)
        val firestore = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser?.email ?: ""

        pastexperience.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val experienceData = mapOf("Experience" to "No previous Experience")
                firestore.collection("Users").document(email)
                    .update(experienceData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Experience marked as 'No previous Experience'", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error updating experience: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
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
                Toast.makeText(this, "Please fill all fields in card ${i + 1}", Toast.LENGTH_SHORT).show()
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

        val email = auth.currentUser?.email ?: return

        val docRef = firestore.collection("Users").document(email)

        docRef.get()
            .addOnSuccessListener { document ->
                val existingList = document.get("Experience") as? MutableList<Map<String, Any>> ?: mutableListOf()
                existingList.addAll(employmentList)

                docRef.update("Experience", existingList)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Experience added successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load existing data: ${it.message}", Toast.LENGTH_LONG).show()
            }
        finish()
    }


}