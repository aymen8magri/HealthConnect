package com.example.healthconnect.data.admin.validateUsers

import com.example.healthconnect.data.models.User

interface UserRepository {

    // -----------------------------
    // User Fetching
    // -----------------------------
    suspend fun getCurrentUser(): User?

    suspend fun getAllUsers(): List<User>

    suspend fun getUserById(userId: String): User?

    suspend fun updateUser(updatedUser: User): Boolean

    // -----------------------------
    // User Validation
    // -----------------------------
    suspend fun verifyUser(userId: String): Boolean

    suspend fun rejectUser(userId: String): Boolean

    // -----------------------------
    // Role & Status Checks
    // -----------------------------
    suspend fun isCurrentUserAdmin(): Boolean

    suspend fun isCurrentUserDoctorVerified(): Boolean

    suspend fun isCurrentUserCoordinatorVerified(): Boolean
}
