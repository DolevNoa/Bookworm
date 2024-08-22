package com.example.bookworm.data.books

interface UserRepository {
    suspend fun getUserProfile(userId: String): UserProfile?
}
