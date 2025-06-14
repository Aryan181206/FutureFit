package com.example.futurefit.Miscellaneous

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.futurefit.R
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate

import com.google.android.material.button.MaterialButton

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Setting : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var editName: EditText
    private lateinit var saveNameBtn: MaterialButton
    private lateinit var currentPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var changePasswordBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting) // Adjust if your file is named differently
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Initialize views
        editName = findViewById(R.id.editName)
        saveNameBtn = findViewById(R.id.saveNameBtn)
        currentPassword = findViewById(R.id.currentPassword)
        newPassword = findViewById(R.id.newPassword)
        changePasswordBtn = findViewById(R.id.changePasswordBtn)

        // Load name into EditText from shared prefs
        editName.setText(sharedPreferences.getString("name", ""))

        // Save name button
        saveNameBtn.setOnClickListener {
            val name = editName.text.toString().trim()
            if (name.isNotEmpty()) {
                updateName(name)
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Change password button
        changePasswordBtn.setOnClickListener {
            val current = currentPassword.text.toString()
            val newPass = newPassword.text.toString()
            if (current.isNotEmpty() && newPass.length >= 6) {
                changePassword(current, newPass)
            } else {
                Toast.makeText(this, "Fill all fields. New password must be 6+ characters.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateName(newName: String) {
        val user = auth.currentUser
        val email = user?.email ?: return

        // Update Firestore
        firestore.collection("Users").document(email).update("Name", newName)
            .addOnSuccessListener {
                // Save to SharedPreferences
                sharedPreferences.edit().putString("name", newName).apply()
                Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update name", Toast.LENGTH_SHORT).show()
            }
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        val email = user?.email ?: return

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Re-authentication failed. Wrong current password.", Toast.LENGTH_SHORT).show()
            }
    }
}
