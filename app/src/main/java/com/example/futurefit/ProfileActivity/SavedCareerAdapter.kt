package com.example.futurefit.ProfileActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.futurefit.DataClass.Career
import com.example.futurefit.ProfileActivity.SavedCareers.SavedCareer
import com.example.futurefit.R

class SavedCareerAdapter(private val careerList: List<SavedCareer>) :
    RecyclerView.Adapter<SavedCareerAdapter.CareerViewHolder>() {

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
    }

    override fun getItemCount(): Int = careerList.size
}
