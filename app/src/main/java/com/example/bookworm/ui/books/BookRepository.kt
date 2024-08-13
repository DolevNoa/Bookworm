package com.example.bookworm.ui.books

import android.util.Log
import com.example.bookworm.data.books.BookRecommendation
import com.example.bookworm.data.books.BookRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookRepositoryImpl : BookRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("bookRecommendation")

    override suspend fun addBookRecommendation(book: BookRecommendation) {
        collectionRef.add(book)
            .addOnSuccessListener {
                // Handle success
                println("Book recommendation added successfully.")
            }
            .addOnFailureListener { e ->
                // Handle failure
                println("Error adding book recommendation: ${e.message}")
            }
    }
override suspend fun getBookRecommendations(): List<BookRecommendation> {
    return try {
        val result = collectionRef.get().await()
        val books = mutableListOf<BookRecommendation>()
        for (document in result) {
            // Log document data
            Log.d("BookRepositoryImpl", "Document data: ${document.data}")
            val book = document.toObject(BookRecommendation::class.java)
            Log.d("BookRepositoryImpl", "Converted BookRecommendation: $book")
            books.add(book)
        }
        Log.d("BookRepositoryImpl", "Books list: $books")
        books
    } catch (e: Exception) {
        // Handle failure
        Log.e("BookRepositoryImpl", "Error getting book recommendations: ${e.message}")
        emptyList()
    }
}

}

