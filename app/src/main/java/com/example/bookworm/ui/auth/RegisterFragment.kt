package com.example.bookworm.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bookworm.MainActivity
import com.example.bookworm.R
import com.example.bookworm.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var navigateToLoginButton: Button

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: AuthViewModel

    private val mainActivity: MainActivity
        get() = activity as MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText
        confirmPasswordEditText = binding.confirmPasswordEditText
        fullNameEditText = binding.fullNameEditText
        registerButton = binding.registerButton
        navigateToLoginButton = binding.navigateToLoginButton

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val fullName = fullNameEditText.text.toString().trim()

            if (validateInput(email, password, confirmPassword, fullName)) {
                createAccount(email, password, fullName)
            }
        }

        navigateToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return view
    }

    private fun validateInput(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String
    ): Boolean {
        if (fullName.isEmpty()) {
            Toast.makeText(context, "Please enter your full name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) { // assuming a minimum password length of 6 characters
            Toast.makeText(context, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun createAccount(email: String, password: String, fullName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration success
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserProfile(userId, fullName)
                    } else {
                        // Handle error
                        Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // If registration fails, display a message to the user
                    Toast.makeText(
                        context,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveUserProfile(userId: String, fullName: String) {
        val userProfile = hashMapOf(
            "fullName" to fullName
        )
        firestore.collection("users").document(userId)
            .set(userProfile)
            .addOnSuccessListener {
                // Profile saved successfully
                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_feedFragment)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to save profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
