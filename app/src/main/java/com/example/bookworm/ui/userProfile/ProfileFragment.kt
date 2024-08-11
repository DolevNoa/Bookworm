package com.example.bookworm.ui.userProfile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.R
import com.example.bookworm.databinding.FragmentProfileBinding
import com.example.bookworm.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
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

        // Populate fields with current user data
        populateUserData()

        // Set up click listeners
        binding.profileImage.setOnClickListener {
            openImagePicker()
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
        }
    }

    private fun populateUserData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            binding.FullNameEditText.setText(user.displayName)
            binding.EmailEditText.setText(user.email)
            // Load profile image using Glide or similar library
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.profileImage.setImageURI(imageUri)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${UUID.randomUUID()}")
        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { url ->
                        updateUserProfile(url.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUserProfile(profileImageUrl: String? = null) {
        val newDisplayName = binding.FullNameEditText.text.toString()
        val newEmail = binding.EmailEditText.text.toString()

        val user = auth.currentUser
        user?.let { firebaseUser ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .apply { profileImageUrl?.let { setPhotoUri(Uri.parse(it)) } }
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
