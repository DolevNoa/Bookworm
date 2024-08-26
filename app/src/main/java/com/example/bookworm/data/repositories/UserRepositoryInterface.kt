package com.example.bookworm.data.repositories

import com.example.bookworm.data.models.UserProfile

interface UserRepositoryInterface {
    suspend fun getUserProfile(userId: String): UserProfile?
}
