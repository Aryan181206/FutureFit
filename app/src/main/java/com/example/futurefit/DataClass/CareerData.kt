package com.example.futurefit.DataClass

data class CareerData(
    val Career_Name: List<Career>
)

data class Career(
    val Career_Name: String,
    val Reason_Fit: String,
    val Skills_to_learn: List<String>,
    val Recommended_courses: List<String>,
    val Match_Percentage: Int
)