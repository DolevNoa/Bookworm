package com.example.bookworm.ui.books

import androidx.lifecycle.ViewModel
import com.example.bookworm.data.books.BookRecommendation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetBooksViewModel : ViewModel() {
    private val repository: BookRepositoryImpl = BookRepositoryImpl()

    // Function to fetch all book recommendations
    suspend fun getBookRecommendations(): List<BookRecommendation> {
        return withContext(Dispatchers.IO) {
            repository.getBookRecommendations()
        }
    }
}
