package com.example.futurefit.ProfileActivity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
class AllSkills : AppCompatActivity() {

    private lateinit var adapter: SkillsAdapter
    private lateinit var recyclerView: RecyclerView
    private val allSkills = mutableListOf<Pair<String, String>>() // Pair<"Soft"/"Technical", skill>
    private var currentFilter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_skills)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        recyclerView = findViewById(R.id.allskillsRV)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = SkillsAdapter(allSkills) { type, skill ->
            showDeleteDialog(type, skill)
        }
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btn_all).setOnClickListener {
            currentFilter = "All"
            filterSkills()
        }
        findViewById<Button>(R.id.btn_technical).setOnClickListener {
            currentFilter = "Technical"
            filterSkills()
        }
        findViewById<Button>(R.id.btn_soft).setOnClickListener {
            currentFilter = "Soft"
            filterSkills()
        }

        // Add swipe to delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val (type, skill) = adapter.getSkillAt(position)
                deleteSkill(type, skill)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)


        fetchSkills()
    }

    private fun fetchSkills() {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return

        db.collection("Users").document(email).get()
            .addOnSuccessListener { document ->
                allSkills.clear()
                val softSkills = document.get("SoftSkills") as? List<*> ?: emptyList<Any>()
                val techSkills = document.get("TechnicalSkills") as? List<*> ?: emptyList<Any>()

                softSkills.filterIsInstance<String>().forEach {
                    allSkills.add("Soft" to it)
                }
                techSkills.filterIsInstance<String>().forEach {
                    allSkills.add("Technical" to it)
                }

                filterSkills()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch skills", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterSkills() {
        val filtered = when (currentFilter) {
            "Technical" -> allSkills.filter { it.first == "Technical" }
            "Soft" -> allSkills.filter { it.first == "Soft" }
            else -> allSkills
        }
        adapter.updateData(filtered)
    }

    private fun showDeleteDialog(type: String, skill: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Skill")
            .setMessage("Delete \"$skill\" from $type Skills?")
            .setPositiveButton("Yes") { _, _ -> deleteSkill(type, skill) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteSkill(type: String, skill: String) {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return

        val field = if (type == "Technical") "TechnicalSkills" else "SoftSkills"
        db.collection("Users").document(email)
            .update(field, FieldValue.arrayRemove(skill))
            .addOnSuccessListener {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                fetchSkills()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
            }
    }
}
