package com.example.bookworm.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookworm.data.books.BookRecommendation
import kotlinx.coroutines.launch

class AddBookViewModel : ViewModel() {
    private val repository: BookRepositoryImpl = BookRepositoryImpl()

    fun addBookRecommendation(book: BookRecommendation) {
        viewModelScope.launch {
            repository.addBookRecommendation(book)
        }
    }
}
