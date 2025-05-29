package com.example.futurefit.Fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.example.futurefit.Miscellaneous.AboutUs
import com.example.futurefit.Miscellaneous.Setting
import com.example.futurefit.ProfileActivity.AllExperience
import com.example.futurefit.ProfileActivity.AllPredictedCareer
import com.example.futurefit.ProfileActivity.AllSkills


import com.example.futurefit.ProfileActivity.SavedCareers
import com.example.futurefit.R
import com.example.futurefit.SplashScreen
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


import java.util.Calendar


class ProfileFrag : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var uploadButton: ImageView
    private lateinit var imageUri: Uri

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            uploadImageToCloudinary(it)
        }
    }


    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    private lateinit var name : TextView
    private lateinit var email : TextView

    private lateinit var dobTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var experience1: TextView

    private lateinit var streamView: TextView
    private lateinit var qualificationView: TextView
    private lateinit var cgpaView: TextView

    private lateinit var fitnessView: TextView

    private lateinit var logicalScoreText: TextView
    private lateinit var numericalScoreText: TextView
    private lateinit var verbalScoreText: TextView


    private lateinit var mbtiPersonalityText: TextView
    private lateinit var personalityDescText: TextView

    private lateinit var interestTextView: TextView


    private lateinit var tabLayout: TabLayout

    private lateinit var personalinfocontentTab1: LinearLayout
    private lateinit var educationdetailcontentTab2: LinearLayout
    private lateinit var progressdetilscontentTab3: LinearLayout
    private lateinit var aboutyoudetilscontentTab4: LinearLayout
    private lateinit var careerdetilscontentTab5: LinearLayout
    private lateinit var experiencedetilscontentTab6: LinearLayout
    private lateinit var skilldetilscontentTab7: LinearLayout

    private lateinit var logoutbtn : CardView

    private lateinit var seeMoreBtn : CardView
    private lateinit var allsaved : CardView
    private lateinit var seeallexperience : TextView
    private lateinit var gotoaboutus : CardView
    private lateinit var gotosetting : CardView
    private lateinit var addfeedback : CardView


    private lateinit var gotoallskills : TextView
    private lateinit var skillTV1 : TextView
    private lateinit var skillTV2 : TextView




    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        gotosetting = view?.findViewById(R.id.gotosetting)!!
        gotosetting.setOnClickListener {
            startActivity(Intent(requireContext(), Setting::class.java))
        }

        gotoaboutus = view?.findViewById(R.id.aboutus)!!
        gotoaboutus.setOnClickListener {
            startActivity(Intent(requireContext(), AboutUs::class.java))
        }


        name = view.findViewById(R.id.username)
        email = view.findViewById(R.id.usermail)

        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        name.text = sharedPreferences.getString("name","")
        email.text = sharedPreferences.getString("email","")

        showname(email, name)

        tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        personalinfocontentTab1 = view.findViewById(R.id.personalinfocontentTab1)
        educationdetailcontentTab2 = view.findViewById(R.id.educationdetailcontentTab2)
        progressdetilscontentTab3 = view.findViewById(R.id.progressdetilscontentTab3)
        aboutyoudetilscontentTab4 = view.findViewById(R.id.aboutyoudetilscontentTab4)
        careerdetilscontentTab5 = view.findViewById(R.id.careerdetilscontentTab5)
        experiencedetilscontentTab6 = view.findViewById(R.id.experiencedetilscontentTab6)
        skilldetilscontentTab7 = view.findViewById(R.id.skillsdetilscontentTab7)


        // Add tabs manually
        tabLayout.addTab(tabLayout.newTab().setText("Personal"))
        tabLayout.addTab(tabLayout.newTab().setText("Education Details"))
        tabLayout.addTab(tabLayout.newTab().setText("Progress"))
        tabLayout.addTab(tabLayout.newTab().setText("About You"))
        tabLayout.addTab(tabLayout.newTab().setText("Career"))
        tabLayout.addTab(tabLayout.newTab().setText("Experience"))
        tabLayout.addTab(tabLayout.newTab().setText("Skills"))

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        personalinfocontentTab1.visibility = View.VISIBLE
                        educationdetailcontentTab2.visibility = View.GONE
                        progressdetilscontentTab3.visibility = View.GONE
                        aboutyoudetilscontentTab4.visibility = View.GONE
                        careerdetilscontentTab5.visibility = View.GONE
                        experiencedetilscontentTab6.visibility = View.GONE
                        skilldetilscontentTab7.visibility = View.GONE
                    }

                    1 -> {
                        personalinfocontentTab1.visibility = View.GONE
                        educationdetailcontentTab2.visibility = View.VISIBLE
                        progressdetilscontentTab3.visibility = View.GONE
                        aboutyoudetilscontentTab4.visibility = View.GONE
                        careerdetilscontentTab5.visibility = View.GONE
                        experiencedetilscontentTab6.visibility = View.GONE
                        skilldetilscontentTab7.visibility = View.GONE
                    }

                    2 -> {
                        personalinfocontentTab1.visibility = View.GONE
                        educationdetailcontentTab2.visibility = View.GONE
                        progressdetilscontentTab3.visibility = View.VISIBLE
                        aboutyoudetilscontentTab4.visibility = View.GONE
                        careerdetilscontentTab5.visibility = View.GONE
                        experiencedetilscontentTab6.visibility = View.GONE
                        skilldetilscontentTab7.visibility = View.GONE
                    }

                    3 -> {
                        personalinfocontentTab1.visibility = View.GONE
                        educationdetailcontentTab2.visibility = View.GONE
                        progressdetilscontentTab3.visibility = View.GONE
                        aboutyoudetilscontentTab4.visibility = View.VISIBLE
                        careerdetilscontentTab5.visibility = View.GONE
                        experiencedetilscontentTab6.visibility = View.GONE
                        skilldetilscontentTab7.visibility = View.GONE
                    }

                    4 -> {
                        personalinfocontentTab1.visibility = View.GONE
                        educationdetailcontentTab2.visibility = View.GONE
                        progressdetilscontentTab3.visibility = View.GONE
                        aboutyoudetilscontentTab4.visibility = View.GONE
                        careerdetilscontentTab5.visibility = View.VISIBLE
                        experiencedetilscontentTab6.visibility = View.GONE
                        skilldetilscontentTab7.visibility = View.GONE
                    }

                    5 -> {
                        personalinfocontentTab1.visibility = View.GONE
                        educationdetailcontentTab2.visibility = View.GONE
                        progressdetilscontentTab3.visibility = View.GONE
                        aboutyoudetilscontentTab4.visibility = View.GONE
                        careerdetilscontentTab5.visibility = View.GONE
                        experiencedetilscontentTab6.visibility = View.VISIBLE
                        skilldetilscontentTab7.visibility = View.GONE
                    }

                    6 -> {
                        personalinfocontentTab1.visibility = View.GONE
                        educationdetailcontentTab2.visibility = View.GONE
                        progressdetilscontentTab3.visibility = View.GONE
                        aboutyoudetilscontentTab4.visibility = View.GONE
                        careerdetilscontentTab5.visibility = View.GONE
                        experiencedetilscontentTab6.visibility = View.GONE
                        skilldetilscontentTab7.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        gotoallskills = view.findViewById(R.id.seeallskill)
        gotoallskills.setOnClickListener {
            startActivity(Intent(requireContext(),AllSkills::class.java))
        }



        addfeedback = view.findViewById(R.id.addfeedback)
        addfeedback.setOnClickListener {
            FeedBackDialogBox(requireContext(), email.toString())

        }


        seeMoreBtn = view.findViewById(R.id.allpredictedcareer)
        seeMoreBtn.setOnClickListener {
            startActivity(Intent(requireContext(),AllPredictedCareer::class.java))
        }
        allsaved = view.findViewById(R.id.allsavedcareer)
        allsaved.setOnClickListener {
            startActivity(Intent(requireContext(),SavedCareers::class.java))
        }

        seeallexperience = view.findViewById(R.id.seeallexp)
        seeallexperience.setOnClickListener {
            startActivity(Intent(requireContext(),AllExperience::class.java))
        }

        profileImageView = view.findViewById(R.id.profileImage)
        uploadButton = view.findViewById(R.id.PersonalPhotoeditIcon)

        uploadButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        loadProfileImageFromFirestore()


        logoutbtn = view.findViewById(R.id.logout)
        logoutbtn.setOnClickListener {
            // Show confirmation dialog before logging out
            AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ ->
                    // Clear Firebase authentication
                    FirebaseAuth.getInstance().signOut()

                    // Clear SharedPreferences
                    val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()

                    // Redirect to SignUp activity and clear activity stack
                    val intent = Intent(requireContext(), SplashScreen ::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }




        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize TextViews
        dobTextView = view.findViewById(R.id.DateofBirth)
        genderTextView = view.findViewById(R.id.Gender)
        phoneTextView = view.findViewById(R.id.PhoneNumber)
        locationTextView = view.findViewById(R.id.Address)
        experience1 = view.findViewById(R.id.exp1)

        // Show loading initially
        dobTextView.text = "Loading..."
        genderTextView.text = "Loading..."
        phoneTextView.text = "Loading..."
        locationTextView.text = "Loading..."
        experience1.text = "Loading..."

        //Initialize TextView

        logicalScoreText = view.findViewById(R.id.AptitdeLogicalScore)
        numericalScoreText = view.findViewById(R.id.AptitdeNumericalScore)
        verbalScoreText = view.findViewById(R.id.AptitdeVerbalScore)


        mbtiPersonalityText = view.findViewById(R.id.mbtipersonality)
        personalityDescText = view.findViewById(R.id.personalitydesc)

        mbtiPersonalityText.text = "Loading..."
        personalityDescText.text = "Loading..."

        interestTextView = view.findViewById(R.id.interest)
        interestTextView.text = "Loading..."


        streamView = view.findViewById<TextView>(R.id.stream)
        streamView.text = "Loading.."
        qualificationView = view.findViewById<TextView>(R.id.qualification)
        qualificationView.text = "Loading.."
        cgpaView = view.findViewById<TextView>(R.id.cgpa)
        cgpaView.text = "Laoding..."
        fitnessView = view.findViewById(R.id.fitnessTextView)
        fitnessView.text = "Loading..."

        // Get current user's email
        val email = auth.currentUser?.email ?: ""

        // Fetch user data from Firestore
        fetchAndDisplayUserData(email)  // Fetch eduaction details from Firestore
        fetchandDisplayEducationData(email)
        fetchAndDisplayFitnessData(email) // 👈 FETCH FITNESS DATA
        fetchAndDisplayAptitudeScores(email)
        fetchPersonalityData(email)
        fetchAndDisplayInterest(email)
        fetchAndDisplayfirstExperience(email)
        fetchandDisplaytwoSkills(email)





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

    private fun showname(email: android.widget.TextView , nameTV : android.widget.TextView) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(email.text.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("Name")
                    nameTV.text = name
                } else {
                    Log.d("Skills", "No such document")
                }
            }
    }

    private fun fetchandDisplaytwoSkills(email: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(email)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val softSkills = document.get("SoftSkills") as? List<String> ?: emptyList()
                    val techSkills = document.get("TechnicalSkills") as? List<String> ?: emptyList()

                    val oneSoftSkill = softSkills.firstOrNull() ?: "No Soft Skill"
                    val oneTechSkill = techSkills.firstOrNull() ?: "No Technical Skill"

                    // Example: display in TextViews
                    skillTV1= view?.findViewById(R.id.skill1)!!
                    skillTV2= view?.findViewById(R.id.skill2)!!


                    skillTV1.text = oneSoftSkill
                    skillTV2.text = oneTechSkill
                } else {
                    Log.d("Skills", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Skills", "get failed with ", exception)
            }
    }


    private fun loadProfileImageFromFirestore() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        val userRef = FirebaseFirestore.getInstance().collection("Users").document(userEmail)

        userRef.get().addOnSuccessListener { document ->
            val url = document.getString("profileImageUrl")
            if (!url.isNullOrEmpty()) {
                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.demoimg)
                    .into(profileImageView)
            }
        }
    }

    private fun uploadImageToCloudinary(uri: Uri) {
        val requestId = MediaManager.get().upload(uri)
            .option("folder", "profile_pics/")
            .option("public_id", FirebaseAuth.getInstance().currentUser?.uid)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Toast.makeText(requireContext(), "Uploading...", Toast.LENGTH_SHORT).show()
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    val imageUrl = resultData["secure_url"].toString()
                    saveImageUrlToFirestore(imageUrl)
                }

                override fun onError(
                    requestId: String?,
                    error: com.cloudinary.android.callback.ErrorInfo?,
                ) {
                    Toast.makeText(requireContext(), "Upload failed: ${error?.description}", Toast.LENGTH_SHORT).show()
                }

                override fun onReschedule(
                    requestId: String?,
                    error: com.cloudinary.android.callback.ErrorInfo?,
                ) {}
            }).dispatch()
    }

    private fun saveImageUrlToFirestore(url: String) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        val userRef = FirebaseFirestore.getInstance().collection("Users").document(userEmail)

        userRef.update("profileImageUrl", url)
            .addOnSuccessListener {
                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.demoimg)
                    .into(profileImageView)
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update Firestore", Toast.LENGTH_SHORT).show()
            }
    }


    private fun fetchAndDisplayfirstExperience(email: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("Users").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("FirestoreDebug", "Document found: ${document.data}")

                    val experienceList = document.get("Experience") as? List<HashMap<String, Any>>
                    Log.d("FirestoreDebug", "Experience List: $experienceList")

                    if (!experienceList.isNullOrEmpty()) {
                        val firstExp = experienceList[0]
                        val company = firstExp["Company"]?.toString() ?: "N/A"
                        val position = firstExp["Position"]?.toString() ?: "N/A"
                        val location = firstExp["Location"]?.toString() ?: "N/A"
                        val years = firstExp["ExperienceYears"]?.toString() ?: "N/A"

                        val result = "$position at $company\n$location · $years year(s)"
                        experience1?.text = result

                        Log.d("FirestoreDebug", "Displayed: $result")
                    } else {
                        Log.d("FirestoreDebug", "Experience list is empty")
                        experience1?.text = "No experience data found"
                    }
                } else {
                    Log.d("FirestoreDebug", "Document does not exist")
                    experience1?.text = "User data not found"
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreDebug", "Failed to fetch document", e)
                experience1?.text = "Failed to load experience"
            }
    }

    private fun fetchAndDisplayInterest(email: String) {
        firestore.collection("Users").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val interest = document.getString("Interest") ?: "Not set"
                    interestTextView.text = interest
                } else {
                    interestTextView.text = "Not set"
                }
            }
            .addOnFailureListener {
                interestTextView.text = "Error loading"
            }
    }


    private fun fetchPersonalityData(email: String) {
        firestore.collection("Users")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val personalityFull = document.getString("Personality")

                    if (!personalityFull.isNullOrEmpty() && personalityFull.contains(" - ")) {
                        val parts = personalityFull.split(" - ")
                        val type = parts[0].trim() // e.g., "INFJ"
                        val description = parts[1].trim() // e.g., "Introversion, Intuition, Feeling, Judging"

                        mbtiPersonalityText.text = type
                        personalityDescText.text = description
                    } else {
                        mbtiPersonalityText.text = "Not set"
                        personalityDescText.text = "Not set"
                    }
                } else {
                    mbtiPersonalityText.text = "Not available"
                    personalityDescText.text = "Not available"
                }
            }
            .addOnFailureListener {
                mbtiPersonalityText.text = "Error"
                personalityDescText.text = "Error loading"
            }
    }


    private fun fetchAndDisplayAptitudeScores(email: String) {
        firestore.collection("Users").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val logicalScore = document.getString("Aptitude_Logical_Score") ?: "0.0 %"
                    val numericalScore = document.getString("Aptitude_Numerical_Score") ?: "0.0 %"
                    val verbalScore = document.getString("Aptitude_Verbal_Score") ?: "0.0 %"

                    logicalScoreText.text = "$logicalScore"
                    numericalScoreText.text = "$numericalScore"
                    verbalScoreText.text = "$verbalScore"


                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load scores", Toast.LENGTH_SHORT).show()
            }
    }



    // 🔥 Fetch Fitness Data
    private fun fetchAndDisplayFitnessData(email: String) {
        firestore.collection("Users").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val status = document.getString("Physical_Fitness") ?: "Not set"
                    fitnessView.text = "Status : $status"
                } else {
                    fitnessView.text = "Status : Not set"
                }
            }
            .addOnFailureListener {
                fitnessView.text = "Status : Error"
            }
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
                    val formattedDate = String.format("%02d/%02d/%04d", day, month + 1, year)
                    dob.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Enhanced Material Design Dialog
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Personal Info")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
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
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
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

        // Enhanced Material Design Dialog
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Education Info")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
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
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
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

private fun FeedBackDialogBox(context: Context, email: String) {
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_feedback, null)
    val feedbackEditText = dialogView.findViewById<EditText>(R.id.editTextFeedback)
    val usermail = FirebaseAuth.getInstance().currentUser?.email

    val dialog = MaterialAlertDialogBuilder(context)
        .setTitle("Share Your Experience")
        .setView(dialogView)
        .setPositiveButton("Submit") { _, _ ->
            val feedbackText = feedbackEditText.text.toString().trim()

            if (feedbackText.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()
                val userDocRef = db.collection("Users").document(usermail.toString())

                // Fetch profile URL
                userDocRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val profileUrl = document.getString("profileImageUrl") ?: ""

                            val feedbackMap = mapOf(
                                "feedcontent" to feedbackText,
                                "profileImageUrl" to profileUrl
                            )

                            // Save to FeedBacks/allfeeds
                            db.collection("FeedBacks")
                                .document("allfeeds")
                                .update("feedbackList", FieldValue.arrayUnion(feedbackMap))
                                .addOnSuccessListener {
                                    Log.d("Feedback", "Added to FeedBacks/allfeeds")
                                }
                                .addOnFailureListener {
                                    // Create doc if doesn't exist
                                    db.collection("FeedBacks")
                                        .document("allfeeds")
                                        .set(mapOf("feedbackList" to arrayListOf(feedbackMap)))
                                }

                            // Save to Users/{email}/FeedBackContent
                            userDocRef.update("FeedBackContent", feedbackText)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Shared Successfully", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to fetch profile URL.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Write something.", Toast.LENGTH_SHORT).show()
            }
        }
        .setNegativeButton("Cancel", null)
        .create()

    dialog.show()
}



