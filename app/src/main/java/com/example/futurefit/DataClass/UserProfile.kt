package com.example.futurefit.DataClass

    data class UserProfile(
        val name: String = "",
        val email: String = "",
        val phone: String = "",
        val dateOfBirth: String = "",
        val gender: String = "",
        val location: String = "",
        val stream: String = "",
        val qualification: String = "",
        val cgpa: String = "",
        val personality: String = "",
        val interest: String = "",
        val aptitudeLogical: String = "",
        val aptitudeNumerical: String = "",
        val aptitudeVerbal: String = "",
        val softSkills: List<String> = emptyList(),
        val technicalSkills: List<String> = emptyList(),
        val experience: List<Map<String, Any>> = emptyList(),
        val aiPredictionResult: String =""
    )

