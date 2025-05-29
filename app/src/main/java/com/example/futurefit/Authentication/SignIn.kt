package com.example.futurefit.Authentication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.example.futurefit.BottomBar
import com.example.futurefit.R
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore

class SignIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var forgotpasswd: TextView

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: CardView
    private lateinit var goToSignUpTextView: TextView
    private lateinit var googleSignInCard: CardView

    private val RC_SIGN_IN = 101

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("en")
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI
        emailEditText = findViewById(R.id.loginEmailEditText)
        passwordEditText = findViewById(R.id.loginPasswordEditText)
        loginButton = findViewById(R.id.loginButton)
        goToSignUpTextView = findViewById(R.id.goToSignUpTextView)
        googleSignInCard = findViewById(R.id.googleSignInCard)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // from strings.xml
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Email/Password Login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            loginUser(email, password)
            finish()
        }

        // Go to SignUp screen
        goToSignUpTextView.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }

        // Google Sign-In
        googleSignInCard.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val email = auth.currentUser?.email ?: ""
        forgotpasswd = findViewById(R.id.forgotpassword)
        forgotpasswd.setOnClickListener {
            showAnimatedForgotPasswordDialog()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchAndSaveUserData(email)
                } else {
                    val error = task.exception?.message ?: "Unknown error"
                    Toast.makeText(this, "Login failed: $error", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun fetchAndSaveUserData(email: String) {
        firestore.collection("Users").document(email).get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: "User"
                saveUserToSharedPreferences(name, email)

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, BottomBar::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val name = user?.displayName ?: "User"
                    val email = user?.email

                    if (email != null) {
                        saveGoogleUserToFirestore(name, email)
                    }
                } else {
                    val error = task.exception?.message ?: "Unknown error"
                    Toast.makeText(this, "Google Sign-In failed: $error", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveGoogleUserToFirestore(name: String, email: String) {
        val userRef = firestore.collection("Users").document(email)
        userRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    val userMap = mapOf("name" to name, "email" to email)
                    userRef.set(userMap)
                }

                saveUserToSharedPreferences(name, email)
                Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, BottomBar::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                } else {
                    Toast.makeText(this, "Google Sign-In returned null account", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveUserToSharedPreferences(name: String, email: String) {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("name", name)
            putString("email", email)
            apply()
        }
    }

    @SuppressLint("CheckResult")
    private fun showAnimatedForgotPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        val emailEditText = dialogView.findViewById<TextInputEditText>(R.id.forgotEmail)
        val resetButton = dialogView.findViewById<MaterialButton>(R.id.resetPasswordBtn)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Forgot Password?")
            .setMessage("Enter your registered email to receive a reset link.")
            .setView(dialogView)
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Reset link sent to $email", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
    }

}

