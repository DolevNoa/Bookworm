package com.example.bookworm.data.models

import com.google.firebase.Timestamp

data class BookRecommendation(
    val timestamp: Timestamp = Timestamp.now(),
    val creator: String = "",
    val bookName: String = "",
    val description: String = "",
    val rating: Float = 0.0f,
    val imageUrl: String = ""
) {}