package com.example.bookworm.data.repositories

import android.util.Log
import com.example.bookworm.data.models.BookRecommendation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookRepositoryImpl : BookRepositoryInterface {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("bookRecommendation")

    override suspend fun addBookRecommendation(book: BookRecommendation) {
        try {
            collectionRef.add(book).await()
            Log.d("BookRepositoryImpl", "Book recommendation added successfully.")
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error adding book recommendation: ${e.message}")
            throw e
        }
    }

    override suspend fun deleteBookRecommendation(book: BookRecommendation) {
        try {
            val querySnapshot = collectionRef
                .whereEqualTo("timestamp", book.timestamp)
                .whereEqualTo("creator", book.creator)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                document.reference.delete().await()
            }

            Log.d("BookRepositoryImpl", "Book recommendation deleted successfully.")

        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error deleting book recommendation: ${e.message}")
            throw e
        }
    }

    override suspend fun editBookRecommendation(book: BookRecommendation) {
        try {
            val querySnapshot = collectionRef
                .whereEqualTo("timestamp", book.timestamp)
                .whereEqualTo("creator", book.creator)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                document.reference.set(book).await()
            }

            Log.d("BookRepositoryImpl", "Book recommendation updated successfully.")
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error updating book recommendation: ${e.message}")
            throw e
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
    suspend fun getBookRecommendations(userId: String): List<BookRecommendation> {
        return try {
            val result = collectionRef
                .whereEqualTo("creator", userId) // Filter posts by creator ID
                .get()
                .await()
            val books = mutableListOf<BookRecommendation>()
            for (document in result) {
                val book = document.toObject(BookRecommendation::class.java)
                books.add(book)
            }
            books
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error getting book recommendations: ${e.message}")
            emptyList()
        }
    }
}

