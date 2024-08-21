// UserRepository.kt
package com.example.bookworm.data.repositories

import android.util.Log
import com.example.bookworm.data.books.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val usersCollection = firestore.collection("users")
    private val profileImagesRef = storage.reference.child("profile_images")


    suspend fun getUserFullName(userId: String): String? {
        return try {
            val document = usersCollection.document(userId).get().await()
            document.getString("fullName")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    // UserRepository.kt
    suspend fun getUserProfilePhotoUrl(userId: String): String? {
        return try {
            val imageRef = profileImagesRef.child("$userId.jpg")
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val fullNameDeferred = getUserFullName(userId)
            val photoUrlDeferred = getUserProfilePhotoUrl(userId)

            if (fullNameDeferred != null && photoUrlDeferred != null) {
                Log.e("HandleBooksViewModel", "User not found for ID: $fullNameDeferred")
                Log.e("HandleBooksViewModel", "User not found for ID: $photoUrlDeferred")
                UserProfile(
                    userId = userId,
                    fullName = fullNameDeferred,
                    photoUrl = photoUrlDeferred
                )
            } else {
                Log.e("HandleBooksViewModel", "User not found for ID: $userId")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
