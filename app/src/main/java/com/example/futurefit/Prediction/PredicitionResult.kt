package com.example.futurefit.Prediction

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.futurefit.DataClass.Career
import com.example.futurefit.DataClass.CareerData
import com.example.futurefit.R
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File

class PredicitionResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_predicition_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fileName = "career_prediction_result.json"
        val file = File(filesDir, fileName)

        if (!file.exists()) {
            Toast.makeText(this, "Prediction file not found!", Toast.LENGTH_LONG).show()
            return
        }

        val jsonString = file.readText()

        val gson = Gson()
        val careerData: CareerData? = try {
            gson.fromJson(jsonString, CareerData::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            Toast.makeText(this, "Error parsing prediction data", Toast.LENGTH_LONG).show()
            return
        }

        if (careerData?.Career_Name == null || careerData.Career_Name.isEmpty()) {
            Toast.makeText(this, "No career prediction data found", Toast.LENGTH_LONG).show()
            return
        }

        val careers = careerData.Career_Name

        if (careers.size >= 3) {
            setCareerCard(careers[0], R.id.careerName1, R.id.reason1, R.id.skills1, R.id.courses1, R.id.match1)
            setCareerCard(careers[1], R.id.careerName2, R.id.reason2, R.id.skills2, R.id.courses2, R.id.match2)
            setCareerCard(careers[2], R.id.careerName3, R.id.reason3, R.id.skills3, R.id.courses3, R.id.match3)
        } else {
            Toast.makeText(this, "Less than 3 career predictions available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCareerCard(
        career: Career,
        nameId: Int,
        reasonId: Int,
        skillsId: Int,
        coursesId: Int,
        matchId: Int
    ) {
        findViewById<TextView>(nameId).text = "Career: ${career.Career_Name}"
        findViewById<TextView>(reasonId).text = "Why it's a fit: ${career.Reason_Fit}"
        findViewById<TextView>(skillsId).text = "Skills to Learn:\n- " + career.Skills_to_learn.joinToString("\n- ")
        findViewById<TextView>(coursesId).text = "Recommended Courses:\n- " + career.Recommended_courses.joinToString("\n- ")
        findViewById<TextView>(matchId).text = "Match %: ${career.Match_Percentage}%"
    }

    private fun showInfoDialog(title: String, content: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_info_popup, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle = dialogView.findViewById<TextView>(R.id.heading)
        val tvContent = dialogView.findViewById<TextView>(R.id.courseContent)

        tvTitle.text = title
        tvContent.text = content

        dialog.show()
    }
}
