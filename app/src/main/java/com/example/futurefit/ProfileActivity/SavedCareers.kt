package com.example.futurefit.ProfileActivity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.*
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SavedCareers : AppCompatActivity() {

    data class SavedCareer(
        val Career_Name: String = "",
        val Reason_Fit: String = "",
        val Skills_to_learn: String = "",
        val Recommended_courses: String = "",
        val Match_Percentage: Int = 0
    )

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedCareerAdapter
    private val careerList = mutableListOf<SavedCareer>()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_saved_careers)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.savedCareerRV)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = SavedCareerAdapter(this, careerList) {
            Toast.makeText(this, "${it.Career_Name} removed", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        setupSwipeToDelete()

        val email = auth.currentUser?.email
        if (email != null) {
            fetchSavedCareers(email)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchSavedCareers(email: String) {
        val docRef = firestore.collection("Users").document(email)
            .collection("SavedCareers").document(email)

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val data = document.data
                data?.values?.forEach { careerData ->
                    if (careerData is Map<*, *>) {
                        val career = SavedCareer(
                            Career_Name = careerData["Career_Name"].toString(),
                            Reason_Fit = careerData["Reason_Fit"].toString(),
                            Skills_to_learn = careerData["Skills_to_learn"].toString(),
                            Recommended_courses = careerData["Recommended_courses"].toString(),
                            Match_Percentage = (careerData["Match_Percentage"] as? Long)?.toInt() ?: 0
                        )
                        careerList.add(career)
                    }
                }
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "No saved careers found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch careers", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val removedCareer = adapter.removeItem(position)
                deleteFromFirestore(removedCareer)
                Toast.makeText(this@SavedCareers, "${removedCareer.Career_Name} deleted", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun deleteFromFirestore(career: SavedCareer) {
        val email = auth.currentUser?.email ?: return
        val docRef = firestore.collection("Users").document(email)
            .collection("SavedCareers").document(email)

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val updatedData = document.data?.toMutableMap() ?: mutableMapOf()
                val keyToRemove = updatedData.entries.find {
                    (it.value as? Map<*, *>)?.get("Career_Name") == career.Career_Name
                }?.key?.toString()

                if (keyToRemove != null) {
                    updatedData.remove(keyToRemove)
                    docRef.set(updatedData)
                }
            }
        }
    }
}
