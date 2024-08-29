package com.example.bookworm.ui.userProfile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.databinding.FragmentProfileBinding
import com.example.bookworm.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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

        // Initialize the pick image launcher
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    // Handle the image URI
                    val imageUri: Uri? = data?.data
                    if (imageUri != null) {
                        this@ProfileFragment.imageUri = data.data
                        binding.profileImage.setImageURI(imageUri)
                    }
                }
            }

        // Populate fields with current user data
        populateUserData()

        // Observe the fullName LiveData from the ViewModel
        viewModel.fullName.observe(viewLifecycleOwner) { fullName ->
            binding.FullNameEditText.setText(fullName)
        }

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

        // Set up the return button
        binding.returnButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun populateUserData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            binding.FullNameEditText.setText(user.displayName)

            // Load profile image if available
            val photoUrl = user.photoUrl
            photoUrl?.let {
                Glide.with(this).load(photoUrl).into(binding.profileImage)
            } ?: run {
                // Set default avatar if no photo URL is available
                binding.profileImage.setImageResource(R.drawable.placeholder_user_image)
            }
        }
    }

    private fun updateUserProfile() {
        val newDisplayName = binding.FullNameEditText.text.toString()

        val user = auth.currentUser
        user?.let { firebaseUser ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build()

            firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        updateFirestoreUserProfile(firebaseUser.uid, newDisplayName)
                        viewModel.updateFullName(newDisplayName) // Update the ViewModel
                    } else {
                        Toast.makeText(context, "Failed to update profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun updateFirestoreUserProfile(userId: String, newDisplayName: String) {
        val userProfile = hashMapOf(
            "fullName" to newDisplayName
        )

        FirebaseFirestore.getInstance().collection("users").document(userId)
            .set(userProfile, SetOptions.merge())  // Use merge to avoid overwriting other fields
            .addOnSuccessListener {
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                navigateBackToSettings()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to update profile in Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
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
                                // Call updateUserProfile to handle both name and image update
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
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pickImageLauncher.launch(intent)
    }

    private fun navigateBackToSettings() {
        findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
