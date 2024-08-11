package com.example.bookworm.ui.userProfile

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.databinding.FragmentProfileBinding
import com.example.bookworm.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

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
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Populate fields with current user data
        populateUserData()

        // Set up click listeners
        binding.profileImage.setOnClickListener {
            // Launch the image picker
            openImageChooser()
        }

        binding.updateButton.setOnClickListener {
            if (imageUri != null) {
                uploadImageToFirebaseStorage()
            } else {
                updateUserProfile()
            }
        }

        binding.cancelButton.setOnClickListener {
            // Reload the user data
            populateUserData()
            // Handle cancel button action
            Toast.makeText(context, "Changes canceled", Toast.LENGTH_SHORT).show()
            // Navigate back or close fragment
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
    }

    private fun populateUserData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            binding.FullNameEditText.setText(user.displayName)
            binding.EmailEditText.setText(user.email)

            // Load profile image if available
            val photoUrl = user.photoUrl
            photoUrl?.let {
                Glide.with(this).load(photoUrl).into(binding.profileImage)
            } ?: run {
                // Set default avatar if no photo URL is available
                binding.profileImage.setImageResource(R.drawable.default_avatar)
            }
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

    private fun uploadImageToFirebaseStorage() {
        val fileRef = storageRef.child("profile_images/${auth.currentUser?.uid}.jpg")
        fileRef.putFile(imageUri!!)
            .addOnSuccessListener {
                // After upload, update the profile with the new image URL
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build()

                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Profile image updated successfully", Toast.LENGTH_SHORT).show()
                                // Update user profile
                                updateUserProfile()
                            } else {
                                Toast.makeText(context, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.profileImage.setImageURI(imageUri)
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
