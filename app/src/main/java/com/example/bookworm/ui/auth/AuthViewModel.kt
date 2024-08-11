package com.example.bookworm.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

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

    fun getUserName(): String {
        return auth.currentUser?.displayName ?: ""
    }

    fun getUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    fun logout() {
        auth.signOut()
    }
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