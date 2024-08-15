package com.example.bookworm.ui.books
import androidx.lifecycle.viewModelScope

import androidx.lifecycle.ViewModel
import com.example.bookworm.data.books.BookRecommendation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth

class GetBooksViewModel : ViewModel() {
    private val repository: BookRepositoryImpl = BookRepositoryImpl()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Function to fetch all book recommendations
    suspend fun getBookRecommendations(): List<BookRecommendation> {
        return withContext(Dispatchers.IO) {
            repository.getBookRecommendations()
        }
    }

    suspend fun getUserBookRecommendations(): List<BookRecommendation> {
        val userId = auth.currentUser?.uid
        if (userId != null) {
             return withContext(Dispatchers.IO) {
                repository.getBookRecommendations(userId)
            }
        }
        return emptyList()
    }
}