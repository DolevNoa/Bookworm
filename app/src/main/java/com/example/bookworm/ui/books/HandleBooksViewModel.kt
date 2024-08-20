package com.example.bookworm.ui.books
import android.util.Log
import androidx.lifecycle.MutableLiveData
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
    public val selectedBookRecommendation=MutableLiveData<BookRecommendation>();

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

    fun addBookRecommendation(book: BookRecommendation) {
        viewModelScope.launch {
            repository.addBookRecommendation(book)
        }
    }
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}