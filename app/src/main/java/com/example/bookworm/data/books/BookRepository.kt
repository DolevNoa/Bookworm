package com.example.bookworm.data.books

interface BookRepository {
    suspend fun addBookRecommendation(book: BookRecommendation)
    suspend fun deleteBookRecommendation(book: BookRecommendation)
    suspend fun editBookRecommendation(book: BookRecommendation)
    suspend fun getBookRecommendations(): List<BookRecommendation>
}
