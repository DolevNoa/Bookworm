package com.example.bookworm.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bookworm.R
import com.example.bookworm.databinding.FragmentSettingsBinding
import com.example.bookworm.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.bookworm.MainActivity
import com.example.bookworm.databinding.FragmentRegisterBinding
import com.example.bookworm.ui.userProfile.ProfileFragment

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        // Populate user data
        val userName = viewModel.getUserName()
        val userEmail = viewModel.getUserEmail()
        binding.userName.text = userName
        binding.email.text = userEmail

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

    private fun signOutAndNavigateToLogin() {
        // Sign out the user
        FirebaseAuth.getInstance().signOut()

        // Navigate to the login fragment
        findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)

        // Show a toast message for feedback
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
