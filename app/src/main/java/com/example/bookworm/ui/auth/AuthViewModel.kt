package com.example.bookworm.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _fullName = MutableLiveData<String>()
    val fullName: LiveData<String> get() = _fullName

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        // Fetch user profile data from Firestore
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fullName = document.getString("fullName")
                    _fullName.value = fullName ?: ""
                }
            }
            .addOnFailureListener {
                _fullName.value = ""
            }

        // Fetch user email directly from FirebaseAuth
        _email.value = auth.currentUser?.email ?: ""
    }

    fun updateFullName(newFullName: String) {
        _fullName.value = newFullName
    }
}