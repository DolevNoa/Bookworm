package com.example.bookworm.ui.userProfile

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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var profileImage: ImageView
    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}