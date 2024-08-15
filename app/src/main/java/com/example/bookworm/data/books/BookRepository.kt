package com.example.bookworm.data.books

interface BookRepository {
    suspend fun addBookRecommendation(book: BookRecommendation)
    suspend fun getBookRecommendations(): List<BookRecommendation>
//    suspend fun getBooksByUser(userId: String): List<BookRecommendation>
}
