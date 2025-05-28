package com.example.futurefit.ProfileActivity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AllExperience : AppCompatActivity() {

    data class Experience(
        val Company: String = "",
        val Position: String = "",
        val Location: String = "",
        val ExperienceYears: Int = 0
    )


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExperienceAdapter
    private val experienceList = mutableListOf<Experience>()
    private lateinit var db: FirebaseFirestore
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_experience)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.allexperienceRV)
        recyclerView.layoutManager = LinearLayoutManager(this)


        db = FirebaseFirestore.getInstance()
        userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        adapter = ExperienceAdapter(experienceList) { experience ->
            showDeleteDialog(experience)
        }
        recyclerView.adapter = adapter

        // Attach swipe-to-delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val experienceToDelete = experienceList[position]

                AlertDialog.Builder(this@AllExperience)
                    .setTitle("Delete Experience")
                    .setMessage("Are you sure you want to delete this experience?")
                    .setPositiveButton("Delete") { _, _ ->
                        deleteExperienceFromFirestore(experienceToDelete)
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
        fetchExperienceData()
    }

    private fun fetchExperienceData() {
        db.collection("Users").document(userEmail).get()
            .addOnSuccessListener { document ->
                experienceList.clear()
                if (document.exists()) {
                    val experienceArray = document.get("Experience") as? List<Map<String, Any>>
                    experienceArray?.forEach { map ->
                        val experience = Experience(
                            Company = map["Company"]?.toString() ?: "",
                            Position = map["Position"]?.toString() ?: "",
                            Location = map["Location"]?.toString() ?: "",
                            ExperienceYears = (map["ExperienceYears"] as? Long)?.toInt() ?: 0
                        )
                        experienceList.add(experience)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun showDeleteDialog(experience: Experience) {
        AlertDialog.Builder(this)
            .setTitle("Delete Experience")
            .setMessage("Are you sure you want to delete this experience?")
            .setPositiveButton("Delete") { _, _ ->
                deleteExperienceFromFirestore(experience)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun deleteExperienceFromFirestore(experience: Experience) {
        val userDocRef = db.collection("Users").document(userEmail)

        val experienceMap = mapOf(
            "Company" to experience.Company,
            "Position" to experience.Position,
            "Location" to experience.Location,
            "ExperienceYears" to experience.ExperienceYears
        )

        userDocRef.update("Experience", FieldValue.arrayRemove(experienceMap))
            .addOnSuccessListener {
                experienceList.remove(experience)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Experience deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error deleting experience", Toast.LENGTH_SHORT).show()
            }
    }


}