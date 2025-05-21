package com.example.futurefit.Fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.futurefit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class ProfileFrag : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // UI elements for displaying user info (optional if used elsewhere)
    private lateinit var dobTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var locationTextView: TextView

    private lateinit var streamView : TextView
    private lateinit var qualificationView : TextView
    private lateinit var cgpaView : TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize TextViews
        dobTextView = view.findViewById(R.id.DateofBirth)
        genderTextView = view.findViewById(R.id.Gender)
        phoneTextView = view.findViewById(R.id.PhoneNumber)
        locationTextView = view.findViewById(R.id.Address)

        // Show loading initially
        dobTextView.text = "Loading..."
        genderTextView.text = "Loading..."
        phoneTextView.text = "Loading..."
        locationTextView.text = "Loading..."


        //Initialize TextView

        streamView = view.findViewById<TextView>(R.id.stream)
        streamView.text = "Loading.."
        qualificationView = view.findViewById<TextView>(R.id.qualification)
        qualificationView.text = "Loading.."
        cgpaView = view.findViewById<TextView>(R.id.cgpa)
        cgpaView.text = "Laoding..."

        // Get current user's email
        val email = auth.currentUser?.email ?: ""

        // Fetch user data from Firestore
        fetchAndDisplayUserData(email)

        // Fetch eduaction details from Firestore
        fetchandDisplayEducationData(email)

        // Set up edit icon listeners
        val personalEditIcon: ImageView = view.findViewById(R.id.PersonalInfoeditIcon)
        personalEditIcon.setOnClickListener {
            showEditPersonalInfoDialog(email)
        }

        val educationEditIcon: ImageView = view.findViewById(R.id.eduactionEdit)
        educationEditIcon.setOnClickListener {
            showEditEducationDialog(email)
        }

        return view
    }


    /**
     * Shows the Edit Dialog for Personal Information
     * Pre-fills current data from Firestore and updates Firestore on Save
     */
    @SuppressLint("MissingInflatedId")
    private fun showEditPersonalInfoDialog(email: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_personal, null)
        val dob = dialogView.findViewById<EditText>(R.id.editDateOfBirth)
        val gender = dialogView.findViewById<EditText>(R.id.editGender)
        val phone = dialogView.findViewById<EditText>(R.id.editPhone)
        val location = dialogView.findViewById<EditText>(R.id.editLocation)

        // Fetch existing data from Firestore and pre-fill the dialog
        firestore.collection("Users").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    dob.setText(document.getString("DateOfBirth") ?: "")
                    gender.setText(document.getString("Gender") ?: "")
                    phone.setText(document.getString("Phone") ?: "")
                    location.setText(document.getString("Location") ?: "")
                }
            }

        // Show Date Picker when DOB field is clicked
        dob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val formattedDate = "$day/${month + 1}/$year"
                    dob.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Show AlertDialog
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Personal Info")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val updatedData = mapOf(
                    "DateOfBirth" to dob.text.toString(),
                    "Gender" to gender.text.toString(),
                    "Phone" to phone.text.toString(),
                    "Location" to location.text.toString()
                )

                firestore.collection("Users").document(email)
                    .update(updatedData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Personal Info Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        fetchAndDisplayUserData(email)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    /**
     * Shows the Edit Dialog for Education Information
     * Pre-fills current data from Firestore and updates Firestore on Save
     */
    @SuppressLint("MissingInflatedId")
    private fun showEditEducationDialog(email: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_education, null)
        val stream = dialogView.findViewById<EditText>(R.id.editStream)
        val qualification = dialogView.findViewById<EditText>(R.id.editQualification)
        val cgpa = dialogView.findViewById<EditText>(R.id.editCGPA)

        // Fetch existing education data
        firestore.collection("Users").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    stream.setText(document.getString("Stream") ?: "")
                    qualification.setText(document.getString("Qualification") ?: "")
                    cgpa.setText(document.getString("CGPA") ?: "")
                }
            }

        // Show AlertDialog
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Education Info")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val updatedData = mapOf(
                    "Stream" to stream.text.toString(),
                    "Qualification" to qualification.text.toString(),
                    "CGPA" to cgpa.text.toString()
                )

                firestore.collection("Users").document(email)
                    .update(updatedData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Education Info Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        fetchandDisplayEducationData(email)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun fetchAndDisplayUserData(email: String) {
        firestore.collection("Users").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    dobTextView.text = "Date Of Birth : ${document.getString("DateOfBirth")}" ?: "Not set"
                    genderTextView.text = "Gender : ${document.getString("Gender")}" ?: "Not set"
                    phoneTextView.text = "Phone No. : ${document.getString("Phone")}" ?: "Not set"
                    locationTextView.text = "Location : ${document.getString("Location")}" ?: "Not set"
                } else {
                    dobTextView.text = "No data"
                    genderTextView.text = "No data"
                    phoneTextView.text = "No data"
                    locationTextView.text = "No data"
                }
            }
            .addOnFailureListener {
                dobTextView.text = "Error"
                genderTextView.text = "Error"
                phoneTextView.text = "Error"
                locationTextView.text = "Error"
            }
    }
    private fun fetchandDisplayEducationData(email : String) {
        firestore.collection("Users").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    streamView.text = "Stream : ${document.getString("Stream")}" ?: "Not set"
                    qualificationView.text = "Qualification : ${document.getString("Qualification")}" ?: "Not set"
                    cgpaView.text = "CGPA : ${document.getString("CGPA")}" ?: "Not set"
                }else{
                    streamView.text = "No data"
                    qualificationView.text = "No data"
                    cgpaView.text = "No data"
                }
            }
            .addOnFailureListener {
                streamView.text = "Error"
                qualificationView.text = "Error"
                cgpaView.text = "Error"
            }
    }


}