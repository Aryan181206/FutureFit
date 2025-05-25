package com.example.futurefit.Util


import com.example.futurefit.DataClass.UserProfile
import java.text.SimpleDateFormat
import java.util.*

fun calculateAge(dobString: String): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dob: Date = sdf.parse(dobString) ?: return "Invalid Date"
    val dobCalendar = Calendar.getInstance()
    val today = Calendar.getInstance()
    dobCalendar.time = dob

    var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    return age.toString()
}

fun generatePromptForGemini(user: UserProfile): String {
    val age = calculateAge(user.dateOfBirth)

    return """
        User Profile
        - Age: $age
        - Gender: ${user.gender}
        - Location: ${user.location}
        - Stream: ${user.stream}
        - Qualification: ${user.qualification}
        - CGPA: ${user.cgpa}
        - Personality Type: ${user.personality}
        - Interests: ${user.interest}
        - Aptitude Scores:
            • Logical Reasoning: ${user.aptitudeLogical}
            • Numerical Ability: ${user.aptitudeNumerical}
            • Verbal Ability: ${user.aptitudeVerbal}
        - Soft Skills: ${user.softSkills.joinToString(", ")}
        - Technical Skills: ${user.technicalSkills.joinToString(", ")}
        - Physical Fitness: ${user.physicalfitness}
        - Work Experience: ${user.experience.joinToString { it["position"].toString() + " at " + it["company"] }}

        Analyze the Profile and Suggest :
        1. Top 3 Career Name.
        2. Why User Fits in this (Short Explaination).
        3. Skills to Learn (Only Name).
        4. Recommended courses.
        5. Match percentage (only number)
        
        Provide the Response in Json Format (make only json format not add any thing else) -> with follwing Arrary Object and key value pair.
        Array Object -> Career_Name
        keys -> 1. Career_Name
                2. Reason_Fit
                3. Skills_to_learn
                4. Recommended_courses
                5. Match_Percentage
                
    """.trimIndent()
}
