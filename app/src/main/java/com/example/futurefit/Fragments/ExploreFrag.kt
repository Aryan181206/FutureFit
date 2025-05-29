package com.example.futurefit.Fragments
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futurefit.Explore.BlogWebview
import com.example.futurefit.Explore.CoursesWebView
import com.example.futurefit.Explore.JobWebView
import com.example.futurefit.Explore.SuccessStoryAdapter
import com.example.futurefit.R
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore


class ExploreFrag : Fragment() {

    data class SuccessStory(
        val feedcontent: String = "",
        val profileImageUrl: String = ""
    )
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SuccessStoryAdapter
    private val storyList = mutableListOf<SuccessStory>()

    private lateinit var gotoblog : MaterialCardView
    private lateinit var gotojob : MaterialCardView
    private lateinit var gotocourses : MaterialCardView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        recyclerView = view.findViewById(R.id.successStoriesRV)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SuccessStoryAdapter(storyList)

        gotocourses = view.findViewById(R.id.tocourses)
        gotocourses.setOnClickListener {
            startActivity(Intent(requireContext(), CoursesWebView::class.java))
        }

        gotojob = view.findViewById(R.id.tojob)
        gotojob.setOnClickListener {
            startActivity(Intent(requireContext(), JobWebView::class.java))
        }

        gotoblog = view.findViewById(R.id.toblog)
        gotoblog.setOnClickListener {
            startActivity(Intent(requireContext(), BlogWebview::class.java))
        }
        recyclerView.adapter = adapter
        fetchSuccessStories()
        return view
    }

    private fun fetchSuccessStories() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("FeedBacks").document("allfeeds")

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val feedbackArray = document.get("feedbackList") as? List<Map<String, Any>>
                    storyList.clear()

                    feedbackArray?.forEach { item ->
                        val content = item["feedcontent"] as? String ?: ""
                        val imageUrl = item["profileImageUrl"] as? String ?: ""
                        storyList.add(SuccessStory(content, imageUrl))
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
            }
    }
}
