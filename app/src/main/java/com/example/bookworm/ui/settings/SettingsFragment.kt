package com.example.bookworm.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bookworm.R
import com.example.bookworm.databinding.FragmentSettingsBinding
import com.example.bookworm.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.bumptech.glide.Glide

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        // Populate user data
        populateUserData()

        // Navigate to profile fragment
        binding.editUserButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
        }

        // Handle log out button click
        binding.button1.setOnClickListener {
            signOutAndNavigateToLogin()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch user profile when the view is created
        viewModel.fetchUserProfile()

        // Populate user data into the UI
        populateUserData()
    }

    private fun populateUserData() {
        viewModel.fullName.observe(viewLifecycleOwner) { userName ->
            binding.userName.text = userName
        }

        viewModel.email.observe(viewLifecycleOwner) { userEmail ->
            binding.email.text = userEmail
        }

        // Load profile image
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.placeholder_user_image)
                    .error(R.drawable.placeholder_user_image)
                    .into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(R.drawable.placeholder_user_image)
            }
        }
    }

    private fun signOutAndNavigateToLogin() {
        // Sign out the user
        FirebaseAuth.getInstance().signOut()

        // Navigate to the login fragment
        findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)

        // Show a toast message for feedback
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}