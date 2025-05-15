package com.example.futurefit.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.example.futurefit.Assessment.InterestSurveyQuiz
import com.example.futurefit.AssessmentInstructions.AptitudeTestInstruction
import com.example.futurefit.AssessmentInstructions.PersonalityTestInstruction
import com.example.futurefit.R


class HomeFrag : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find CardView by its ID
        val AssessmentAptitudeTest = view.findViewById<CardView>(R.id.AssessmentAptitudeTest)
        val AssessmentPersonalityTest = view.findViewById<CardView>(R.id.AssessmentPersonalityTest)
        val AssessmentInterestTest = view.findViewById<CardView>(R.id.AssessmentInterestTest)

        AssessmentAptitudeTest.setOnClickListener {
            val intent = Intent(requireContext(), AptitudeTestInstruction ::class.java)
            startActivity(intent)
        }

        AssessmentPersonalityTest.setOnClickListener {
            startActivity(Intent(requireContext(), PersonalityTestInstruction :: class.java))
        }

        AssessmentInterestTest.setOnClickListener {
            startActivity(Intent(requireContext(), InterestSurveyQuiz :: class.java))  //
        }

        return view
    }
}
