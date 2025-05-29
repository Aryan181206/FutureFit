package com.example.futurefit.ProfileActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.example.futurefit.R

class SkillsAdapter(
    private var skillList: List<Pair<String, String>>, // Pair<SkillType, SkillName>
    private val onLongClick: (String, String) -> Unit
) : RecyclerView.Adapter<SkillsAdapter.SkillViewHolder>() {

    inner class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val skillText: TextView = itemView.findViewById(R.id.skill_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skill_card, parent, false)
        return SkillViewHolder(view)
    }

    override fun getItemCount(): Int = skillList.size

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val (type, skill) = skillList[position]
        holder.skillText.text = "$skill ($type Skill)"
        holder.itemView.setOnLongClickListener {
            onLongClick(type, skill)
            true
        }
    }

    fun updateData(newList: List<Pair<String, String>>) {
        skillList = newList
        notifyDataSetChanged()
    }
    fun getSkillAt(position: Int): Pair<String, String> {
        return skillList[position]
    }

}
