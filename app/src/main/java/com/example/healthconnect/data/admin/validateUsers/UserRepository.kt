package com.example.healthconnect.data.admin.validateUsers

import com.example.healthconnect.data.models.User

// data/repository/UserRepository.kt
interface UserRepository {
    // Fetch all users for the list
    suspend fun getAllUsers(): List<User>

    // Fetch a single user by id
    suspend fun getUserById(userId: String): User?

    // Verify a user (sets status to VALIDATED)
    suspend fun verifyUser(userId: String): Boolean

    // Reject a user (sets status to REJECTED)
    suspend fun rejectUser(userId: String): Boolean
}