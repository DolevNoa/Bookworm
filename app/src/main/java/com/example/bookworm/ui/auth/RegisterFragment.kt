package com.example.bookworm.ui.auth

import android.os.Bundle
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
    companion object {
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }

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
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val fullName = fullNameEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && fullName.isNotEmpty()) {
                if (password == confirmPassword) {
                    createAccount(email, password, fullName)
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        navigateToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return view
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
                // Handle error
                Toast.makeText(context, "Failed to save profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
