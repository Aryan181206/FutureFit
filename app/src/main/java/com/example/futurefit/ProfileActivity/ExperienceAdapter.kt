package com.example.futurefit.ProfileActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.futurefit.ProfileActivity.AllExperience.Experience
import com.example.futurefit.R

class ExperienceAdapter(
    private val experienceList: List<Experience>,
    private val onLongPressDelete: (Experience) -> Unit) :
    RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    inner class ExperienceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyTV: TextView = itemView.findViewById(R.id.companyNameTV)
        val positionTV: TextView = itemView.findViewById(R.id.jobTitleTV)
        val locationTV: TextView = itemView.findViewById(R.id.locationTV)
        val yearsTV: TextView = itemView.findViewById(R.id.yearsTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.experience_item, parent, false)
        return ExperienceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        val experience = experienceList[position]
        holder.companyTV.text = "Company: ${experience.Company}"
        holder.positionTV.text = "Position: ${experience.Position}"
        holder.locationTV.text = "Location: ${experience.Location}"
        holder.yearsTV.text = "Years: ${experience.ExperienceYears}"

        holder.itemView.setOnLongClickListener {
            onLongPressDelete(experience)
            true
        }
    }

    override fun getItemCount(): Int = experienceList.size
}
