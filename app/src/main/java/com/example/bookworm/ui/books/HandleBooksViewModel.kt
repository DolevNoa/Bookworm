package com.example.bookworm.ui.books
import androidx.lifecycle.viewModelScope

import androidx.lifecycle.ViewModel
import com.example.bookworm.data.books.BookRecommendation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HandleBooksViewModel : ViewModel() {
    private val repository: BookRepositoryImpl = BookRepositoryImpl()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                // Handle error (e.g., log it)
                false
            }
        }
    }
    fun editBookRecommendation(book: BookRecommendation) {
        viewModelScope.launch {
            try {
                repository.editBookRecommendation(book)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    fun addBookRecommendation(book: BookRecommendation) {
        viewModelScope.launch {
            repository.addBookRecommendation(book)
        }
    }
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}