package com.example.bookworm.ui.books
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import androidx.lifecycle.ViewModel
import com.example.bookworm.data.models.BookRecommendation
import com.example.bookworm.data.models.UserProfile
import com.example.bookworm.data.repositories.BookRepositoryImpl
import com.example.bookworm.data.repositories.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HandleBooksViewModel : ViewModel() {
    private val repository: BookRepositoryImpl = BookRepositoryImpl()
    private val repositoryUser: UserRepositoryImpl = UserRepositoryImpl()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val selectedBookRecommendation = MutableLiveData<BookRecommendation>()

    // Function to fetch all book recommendations
    suspend fun getBookRecommendations(): List<BookRecommendation> {
        return withContext(Dispatchers.IO) {
            repository.getBookRecommendations()
        }
    }

    suspend fun getUserBookRecommendations(): List<BookRecommendation> {
        val userId = getCurrentUserId()
        if (userId != null) {
            return withContext(Dispatchers.IO) {
                repository.getBookRecommendations(userId)
            }
        }
        return emptyList()
    }

    suspend fun deleteBookRecommendation(book: BookRecommendation): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                repository.deleteBookRecommendation(book)
                true
            } catch (e: Exception) {
                Log.e("HandleBooksViewModel", "Error adding book recommendation", e)
                false
            }
        }
    }

    suspend fun editBookRecommendation(bookRecommendation: BookRecommendation): Boolean {
        return try {
            // Update the book recommendation in the database
            repository.editBookRecommendation(bookRecommendation)
            true
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error updating book recommendation", e)
            false
        }
    }

    fun addBookRecommendation(book: BookRecommendation, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.addBookRecommendation(book)
                onComplete(true)
            } catch (e: Exception) {
                Log.e("HandleBooksViewModel", "Error adding book recommendation: ${e.message}")
                onComplete(false)
            }
        }
    }


    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return withContext(Dispatchers.IO) {
            repositoryUser.getUserProfile(userId)
        }
    }
}