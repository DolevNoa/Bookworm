package com.example.bookworm.data.repositories

import com.example.bookworm.data.models.BookRecommendation

interface BookRepositoryInterface {
    suspend fun addBookRecommendation(book: BookRecommendation)
    suspend fun deleteBookRecommendation(book: BookRecommendation)
    suspend fun editBookRecommendation(book: BookRecommendation)
    suspend fun getBookRecommendations(): List<BookRecommendation>
}
