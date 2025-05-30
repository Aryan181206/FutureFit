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

    private val technicalSkills = listOf(

        "Android Development", "AWS", "Azure", "Big Data", "C", "C#", "C++", "Cloud Computing", "CSS",
        "Cybersecurity", "Data Analysis", "Data Engineering", "Data Mining", "Data Science", "Django",
        "Docker", "Express.js", "Firebase", "Flutter", "Git", "GitHub", "Go", "GraphQL", "HTML", "Java",
        "JavaScript", "Jenkins", "Kotlin", "Kubernetes", "Linux", "Machine Learning", "MongoDB", "MySQL",
        "Node.js", "NumPy", "Pandas", "Perl", "PHP", "PostgreSQL", "Python", "R", "React", "React Native",
        "REST APIs", "Ruby", "Rust", "SQL", "Spring Boot", "Swift", "TensorFlow", "TypeScript", "Unity",
        "Visual Basic", "Vue.js", "Web Development", "Other"
    )
    private val softSkills = listOf(

        "Adaptability", "Attention to Detail", "Collaboration", "Communication", "Conflict Resolution",
        "Creativity", "Critical Thinking", "Customer Service", "Decision Making", "Delegation",
        "Emotional Intelligence", "Empathy", "Flexibility", "Interpersonal Skills", "Leadership",
        "Listening", "Management", "Motivation", "Multitasking", "Negotiation", "Networking",
        "Open-mindedness", "Organization", "Patience", "People Management", "Perseverance", "Planning",
        "Positive Attitude", "Presentation Skills", "Problem Solving", "Professionalism", "Public Speaking",
        "Resilience", "Responsibility", "Self-awareness", "Self-confidence", "Self-motivation",
        "Stress Management", "Strategic Thinking", "Strong Work Ethic", "Team Building", "Teamwork",
        "Time Management", "Tolerance", "Trustworthiness", "Verbal Communication", "Visual Communication",
        "Willingness to Learn", "Work Under Pressure", "Other"
    )

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
        val cardView = CardView(context).apply {
            radius = 24f
            cardElevation = 8f
            setContentPadding(28, 28, 28, 28)
            useCompatPadding = true
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(28, 16, 28, 16)
            }
        }

        val innerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val skillList = if (type == "technical") technicalSkills else softSkills
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, skillList)

        val skillInput = AutoCompleteTextView(context).apply {
            hint = "Select or type ${type.replaceFirstChar { it.uppercase() }} Skill"
            setAdapter(adapter)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Show dropdown immediately when touched
            setOnTouchListener { _, _ ->
                if (!isPopupShowing) showDropDown()
                false
            }
        }

        val removeButton = Button(context).apply {
            text = "Remove"
            setBackgroundColor(resources.getColor(R.color.dark_gray, null))
            setTextColor(resources.getColor(R.color.white, null))
            setOnClickListener {
                positionContainer.removeView(cardView)
            }
        }

        innerLayout.addView(skillInput)
        innerLayout.addView(removeButton)
        cardView.addView(innerLayout)
        positionContainer.addView(cardView)

        skillInput.setOnItemClickListener { _, _, position, _ ->
            val selected = adapter.getItem(position).toString()
            if (selected == "Other") {
                val customInput = EditText(context).apply {
                    hint = "Enter custom skill"
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                // Add the EditText below skillInput
                if (innerLayout.childCount == 2) {
                    innerLayout.addView(customInput, 1)
                }
                customInput.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        val customSkill = customInput.text.toString().trim()
                        if (customSkill.isNotEmpty()) {
                            if (type == "technical" && !selectedTechnicalSkills.contains(customSkill)) {
                                selectedTechnicalSkills.add(customSkill)
                            } else if (type == "soft" && !selectedSoftSkills.contains(customSkill)) {
                                selectedSoftSkills.add(customSkill)
                            }
                        }
                    }
                }
            } else {
                if (type == "technical" && !selectedTechnicalSkills.contains(selected)) {
                    selectedTechnicalSkills.add(selected)
                } else if (type == "soft" && !selectedSoftSkills.contains(selected)) {
                    selectedSoftSkills.add(selected)
                }
            }
        }
    }



    private fun saveSkillsToFirestore() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email ?: return
            val userDocRef = firestore.collection("Users").document(email)

            // Extract additional custom skills from EditTexts
            for (i in 0 until positionContainer.childCount) {
                val view = positionContainer.getChildAt(i)
                if (view is LinearLayout) {
                    for (j in 0 until view.childCount) {
                        val innerView = view.getChildAt(j)
                        if (innerView is EditText) {
                            val input = innerView.text.toString().trim()
                            if (input.isNotEmpty()) {
                                if (!selectedTechnicalSkills.contains(input) && !technicalSkills.contains(input)) {
                                    selectedTechnicalSkills.add(input)
                                } else if (!selectedSoftSkills.contains(input) && !softSkills.contains(input)) {
                                    selectedSoftSkills.add(input)
                                }
                            }
                        }
                    }
                }
            }

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
