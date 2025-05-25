package com.example.futurefit.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.futurefit.AddSEC.AddExperience
import com.example.futurefit.AddSEC.AddSkillss
import com.example.futurefit.AssessmentInstructions.AptitudeTestInstruction
import com.example.futurefit.AssessmentInstructions.InterestTestInstruction
import com.example.futurefit.AssessmentInstructions.PersonalityTestInstruction
import com.example.futurefit.ProfileStatus
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeFrag : Fragment() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        // Find CardView by its ID
        val startassessment = view.findViewById<CardView>(R.id.startassessment)
        val AssessmentAptitudeTest = view.findViewById<CardView>(R.id.AssessmentAptitudeTest)
        val AssessmentPersonalityTest = view.findViewById<CardView>(R.id.AssessmentPersonalityTest)
        val AssessmentInterestTest = view.findViewById<CardView>(R.id.AssessmentInterestTest)
        val ProvideSkills = view.findViewById<CardView>(R.id.ProvideSkills)
        val ProvideExperience = view.findViewById<CardView>(R.id.ProvideExperience)

        val profileStatus = view.findViewById<CardView>(R.id.profilestatus)
        val cardPhysicalFitness = view.findViewById<CardView>(R.id.ProvideFitness)

        profileStatus.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileStatus::class.java))
        }

        startassessment.setOnClickListener {
            startActivity(Intent(requireContext(),AptitudeTestInstruction::class.java))
        }

        AssessmentAptitudeTest.setOnClickListener {
            val intent = Intent(requireContext(), AptitudeTestInstruction ::class.java)
            startActivity(intent)
        }

        AssessmentPersonalityTest.setOnClickListener {
            val intent = Intent(requireContext(), PersonalityTestInstruction ::class.java)
            startActivity(intent)
        }

        AssessmentInterestTest.setOnClickListener {
            val intent = Intent(requireContext(), InterestTestInstruction ::class.java)
            startActivity(intent)
        }

        ProvideSkills.setOnClickListener {
            val intent = Intent(requireContext(), AddSkillss :: class.java)
            startActivity(intent)
        }

        ProvideExperience.setOnClickListener {
            val intent = Intent(requireContext(), AddExperience :: class.java)
            startActivity(intent)
        }



        // 🔹 Physical Fitness Dialog
        cardPhysicalFitness.setOnClickListener {
            showFitnessDialog(requireContext())
        }


        return view
    }

    private fun showFitnessDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_physical_fitness, null)
        val spinner = dialogView.findViewById<Spinner>(R.id.fitness_spinner)
        val saveButton = dialogView.findViewById<Button>(R.id.btn_save_fitness)

        val fitnessLevels = listOf("Good", "Better", "Best")
        spinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, fitnessLevels)

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        saveButton.setOnClickListener {
            val selectedFitness = spinner.selectedItem.toString()
            val email = auth.currentUser?.email ?: return@setOnClickListener

            firestore.collection("Users").document(email)
                .update("Physical_Fitness", selectedFitness)
                .addOnSuccessListener {
                    Toast.makeText(context, "Fitness level saved: $selectedFitness", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error saving fitness level", Toast.LENGTH_SHORT).show()
                }
        }

        alertDialog.show()
    }
}
