package com.example.futurefit.ProfileActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.futurefit.ProfileActivity.AllPredictedCareer.PredictedCareer
import com.example.futurefit.R
import com.google.android.material.button.MaterialButton
import org.w3c.dom.Text


class CareerAdapter(private val careerList: List<PredictedCareer>) :
    RecyclerView.Adapter<CareerAdapter.CareerViewHolder>() {

    class CareerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val careerName: TextView = itemView.findViewById(R.id.careerName)
        val matchPercentage: TextView = itemView.findViewById(R.id.matchPercentage)
        val reasonFit: TextView = itemView.findViewById<TextView>(R.id.reasonFit)
        val courses: TextView = itemView.findViewById<TextView>(R.id.courses)
        val skills: TextView = itemView.findViewById(R.id.skillstolearn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CareerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.career_card, parent, false)
        return CareerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CareerViewHolder, position: Int) {
        val item = careerList[position]
        holder.careerName.text = item.careerName
        holder.matchPercentage.text = "Match: ${item.matchPercentage}%"
        holder.reasonFit.text = item.reasonFit
        holder.courses.text = "Recommended Courses:\n${item.recommendedCourses.joinToString("\n")}"
        holder.skills.text = "Skills to Learn:\n${item.skillsToLearn.joinToString("\n")}"
    }

    override fun getItemCount(): Int = careerList.size
}
