package com.example.bookworm.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _navigationEvent = MutableLiveData<Event<NavigationDestination>>()
    val navigationEvent: LiveData<Event<NavigationDestination>> = _navigationEvent

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun navigateTo(destination: NavigationDestination) {
        _navigationEvent.value = Event(destination)
    }

    enum class NavigationDestination {
        LOGIN, REGISTER, OUT, MAIN
    }

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
            .addOnFailureListener { exception ->
                _fullName.value = ""
            }

        // Fetch user email directly from FirebaseAuth
        _email.value = auth.currentUser?.email ?: ""
    }

    // Optionally, you can directly use auth for fetching email
    fun getUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    fun logout() {
        auth.signOut()
    }

    // Event wrapper to handle one-time events
    class Event<out T>(private val content: T) {
        var hasBeenHandled = false
            private set

        fun getContentIfNotHandled(): T? {
            return if (hasBeenHandled) {
                null
            } else {
                hasBeenHandled = true
                content
            }
        }

        fun peekContent(): T = content
    }
}

//    fun getBooksByCurrentUser() = viewModelScope.launch {
//        currUser.value?.uid?.let { userId ->
//            repository.getBooksByUser(userId)
//        }
//    }