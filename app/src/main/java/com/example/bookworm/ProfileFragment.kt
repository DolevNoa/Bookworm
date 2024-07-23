package com.example.yourapp

import android.content.Context
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

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        profileImage = view.findViewById(R.id.profileImage)
        fullNameEditText = view.findViewById(R.id.FullNameEditText)
        emailEditText = view.findViewById(R.id.EmailEditText)
        phoneEditText = view.findViewById(R.id.PhoneEditText)
        saveButton = view.findViewById(R.id.button)

        // Set click listener for the profile image to change the picture
        profileImage.setOnClickListener {
            // Implement an image picker here
        }

        // Set click listener for the save button to save the profile information
        saveButton.setOnClickListener {
            saveUserProfile()
        }

        return view
    }

    private fun saveUserProfile() {
        val fullName = fullNameEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()

        // Save data to SharedPreferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("USER_FULL_NAME", fullName)
            putString("USER_EMAIL", email)
            putString("USER_PHONE", phone)
            apply()
        }

        // Show a confirmation message
        Toast.makeText(activity, "Profile saved", Toast.LENGTH_SHORT).show()
    }
}
