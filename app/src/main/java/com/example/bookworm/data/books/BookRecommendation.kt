package com.example.bookworm.data.books

import com.google.firebase.Timestamp

data class BookRecommendation(
    val timestamp: Timestamp = Timestamp.now(), // Provide a default value
    val creator: String = "",
    val bookName: String = "",
    val description: String = "",
    val rating: Float = 0.0f,
    val imageUrl: String = ""
) {}