package com.example.futurefit.Explore


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.futurefit.Fragments.ExploreFrag.SuccessStory
import com.example.futurefit.R
import de.hdodenhof.circleimageview.CircleImageView

class SuccessStoryAdapter(private val storyList: List<SuccessStory>) :
    RecyclerView.Adapter<SuccessStoryAdapter.StoryViewHolder>() {

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: CircleImageView = itemView.findViewById(R.id.profileImage)
        val feedbackText: TextView = itemView.findViewById(R.id.feedbackContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.succes_stories_card, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]
        holder.feedbackText.text = story.feedcontent
        Glide.with(holder.itemView.context)
            .load(story.profileImageUrl)
            .placeholder(R.drawable.demoimg)
            .into(holder.profileImage)
    }

    override fun getItemCount(): Int = storyList.size
}
