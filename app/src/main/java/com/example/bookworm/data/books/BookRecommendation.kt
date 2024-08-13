package com.example.bookworm.data.books

import com.google.firebase.Timestamp

data class BookRecommendation(
    val timestamp: Timestamp,
    val creator: String,
    val bookName: String,
    val description: String,
    val rating: Float,
    val imageUrl: String
)
