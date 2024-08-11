package com.example.bookworm.ui.userProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookworm.R
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.databinding.FragmentProfileBinding
import com.example.bookworm.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        auth = FirebaseAuth.getInstance()

        // Populate fields with current user data
        populateUserData()

        // Set up click listeners
        binding.profileImage.setOnClickListener {
            // Implement image picker functionality here
        }

        binding.button.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun populateUserData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            binding.FullNameEditText.setText(user.displayName)
            binding.EmailEditText.setText(user.email)
        }
    }

    private fun updateUserProfile() {
        val newDisplayName = binding.FullNameEditText.text.toString()
        val newEmail = binding.EmailEditText.text.toString()

        val user = auth.currentUser
        user?.let { firebaseUser ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build()

            firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Update email if changed
                        if (newEmail != firebaseUser.email) {
                            firebaseUser.updateEmail(newEmail)
                                .addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to update email: ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(context, "Failed to update profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}