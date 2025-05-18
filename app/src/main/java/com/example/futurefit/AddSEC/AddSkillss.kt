package com.example.futurefit.AddSEC

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddSkillss : AppCompatActivity() {

    private lateinit var addSkillCard: CardView
    private lateinit var saveSkillsCard: CardView
    private lateinit var positionContainer: LinearLayout

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val technicalSkills = listOf("Select Technical Skill", "Java", "Kotlin", "Python", "C++", "HTML", "SQL", "Other")
    private val softSkills = listOf("Select Soft Skill", "Communication", "Teamwork", "Leadership", "Time Management", "Adaptability", "Other")

    private val selectedTechnicalSkills = mutableListOf<String>()
    private val selectedSoftSkills = mutableListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_skillss)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        addSkillCard = findViewById(R.id.addSkillsCard)
        saveSkillsCard = findViewById(R.id.saveChanges)
        positionContainer = findViewById(R.id.positionContainer)

        addSkillCard.setOnClickListener {
            showSkillTypeDialog()
        }

        saveSkillsCard.setOnClickListener {
            if (selectedTechnicalSkills.isEmpty() && selectedSoftSkills.isEmpty()) {
                Toast.makeText(this, "Please select at least one skill", Toast.LENGTH_SHORT).show()
            } else {
                saveSkillsToFirestore()
            }
        }
    }

    private fun showSkillTypeDialog() {
        val options = arrayOf("Technical Skills", "Soft Skills")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Skill Type")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> addSkillInput("technical")
                1 -> addSkillInput("soft")
            }
        }
        builder.show()
    }

    private fun addSkillInput(type: String) {
        val context = this
        val horizontalLayout = LinearLayout(context)
        horizontalLayout.orientation = LinearLayout.HORIZONTAL
        horizontalLayout.setPadding(30, 10, 30, 10)
        horizontalLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val spinner = Spinner(context)
        val options = if (type == "technical") technicalSkills else softSkills

        val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, options) {
            override fun isEnabled(position: Int): Boolean = position != 0

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                tv.setTextColor(if (position == 0) resources.getColor(R.color.white) else resources.getColor(R.color.black))
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val removeButton = Button(context).apply {
            text = "Remove"
            setOnClickListener {
                positionContainer.removeView(horizontalLayout)
            }
        }

        horizontalLayout.addView(spinner)
        horizontalLayout.addView(removeButton)
        positionContainer.addView(horizontalLayout)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selected = parent?.getItemAtPosition(pos).toString()

                if (selected == "Other") {
                    val inputLayout = LinearLayout(context).apply {
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(30, 10, 30, 10)
                    }

                    val editText = EditText(context).apply {
                        hint = "Enter your custom skill"
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    }

                    val removeBtn = Button(context).apply {
                        text = "Remove"
                        setOnClickListener {
                            positionContainer.removeView(inputLayout)
                        }
                    }

                    inputLayout.addView(editText)
                    inputLayout.addView(removeBtn)

                    positionContainer.removeView(horizontalLayout)
                    positionContainer.addView(inputLayout)

                    editText.setOnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            val input = editText.text.toString().trim()
                            if (input.isNotEmpty()) {
                                if (type == "technical" && !selectedTechnicalSkills.contains(input)) {
                                    selectedTechnicalSkills.add(input)
                                } else if (type == "soft" && !selectedSoftSkills.contains(input)) {
                                    selectedSoftSkills.add(input)
                                }
                            }
                        }
                    }
                } else if (pos != 0) {
                    if (type == "technical" && !selectedTechnicalSkills.contains(selected)) {
                        selectedTechnicalSkills.add(selected)
                    } else if (type == "soft" && !selectedSoftSkills.contains(selected)) {
                        selectedSoftSkills.add(selected)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun saveSkillsToFirestore() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email ?: return
            val userDocRef = firestore.collection("Users").document(email)

            userDocRef.get().addOnSuccessListener { document ->
                val oldTechSkills = (document.get("TechnicalSkills") as? List<String>) ?: emptyList()
                val oldSoftSkills = (document.get("SoftSkills") as? List<String>) ?: emptyList()

                val updatedTechSkills = (oldTechSkills + selectedTechnicalSkills).distinct()
                val updatedSoftSkills = (oldSoftSkills + selectedSoftSkills).distinct()

                val dataToUpdate = mapOf(
                    "TechnicalSkills" to updatedTechSkills,
                    "SoftSkills" to updatedSoftSkills
                )

                userDocRef.update(dataToUpdate)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Skills saved successfully!", Toast.LENGTH_SHORT).show()
                        selectedTechnicalSkills.clear()
                        selectedSoftSkills.clear()
                        positionContainer.removeAllViews()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save skills: ${e.message}", Toast.LENGTH_LONG).show()
                    }

            }.addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve existing data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
