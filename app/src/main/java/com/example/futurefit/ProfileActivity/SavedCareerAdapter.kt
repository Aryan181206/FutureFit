package com.example.futurefit.ProfileActivity

import android.app.AlertDialog
import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.futurefit.ProfileActivity.SavedCareers.SavedCareer
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SavedCareerAdapter(
    private val context: Context,
    private val careerList: MutableList<SavedCareer>,
    private val onItemRemoved: (SavedCareer) -> Unit
) : RecyclerView.Adapter<SavedCareerAdapter.CareerViewHolder>() {

    class CareerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val careerName: TextView = itemView.findViewById(R.id.careerName)
        val reasonFit: TextView = itemView.findViewById(R.id.reasonFit)
        val courses: TextView = itemView.findViewById(R.id.courses)
        val skillsToLearn: TextView = itemView.findViewById(R.id.skillstolearn)
        val matchPercentage: TextView = itemView.findViewById(R.id.matchPercentage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressMatch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CareerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.career_card, parent, false)
        return CareerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CareerViewHolder, position: Int) {
        val career = careerList[position]
        holder.careerName.text = "Career: ${career.Career_Name}"
        holder.reasonFit.text = career.Reason_Fit
        holder.courses.text = career.Recommended_courses
        holder.skillsToLearn.text = career.Skills_to_learn
        holder.matchPercentage.text = "${career.Match_Percentage}%"
        holder.progressBar.progress = career.Match_Percentage

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context)
                .setTitle("Remove Career")
                .setMessage("Are you sure you want to remove this saved career?")
                .setPositiveButton("Yes") { _, _ ->
                    val removedCareer = careerList[position]
                    deleteFromFirestore(removedCareer)
                    careerList.removeAt(position)
                    notifyItemRemoved(position)
                    onItemRemoved(removedCareer)
                }
                .setNegativeButton("No", null)
                .show()
            true
        }
    }

    private fun deleteFromFirestore(career: SavedCareer) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
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
    fun removeItem(position: Int): SavedCareer {
        val removedCareer = careerList[position]
        careerList.removeAt(position)
        notifyItemRemoved(position)
        return removedCareer
    }


    override fun getItemCount(): Int = careerList.size
}
