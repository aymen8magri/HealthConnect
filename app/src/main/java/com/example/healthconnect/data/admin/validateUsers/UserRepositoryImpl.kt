package com.example.healthconnect.data.admin.validateUsers

import com.example.healthconnect.data.models.Roles
import com.example.healthconnect.data.models.Status
import com.example.healthconnect.data.models.User
import com.google.android.play.integrity.internal.u
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val userCollection = firestore.collection("users")
    private val auth = Firebase.auth

    // ---------------------------------------------------------
    //  Get Current User
    // ---------------------------------------------------------
    override suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return userCollection.document(uid).get().await().toObject(User::class.java)
    }

    // ---------------------------------------------------------
    //  Get All Users
    // ---------------------------------------------------------
    override suspend fun getAllUsers(): List<User> {
        return userCollection.get().await().toObjects(User::class.java)
    }

    // ---------------------------------------------------------
    //  Get User By ID
    // ---------------------------------------------------------
    override suspend fun getUserById(userId: String): User? {
        return userCollection.document(userId).get().await().toObject(User::class.java)
    }

    // ---------------------------------------------------------
    //  Verify User → Status = VALIDATED
    // ---------------------------------------------------------
    override suspend fun verifyUser(userId: String): Boolean {
        return try {
            userCollection.document(userId)
                .update("statusRole", Status.VALIDATED)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ---------------------------------------------------------
    //  Reject User → Status = REJECTED
    // ---------------------------------------------------------
    override suspend fun rejectUser(userId: String): Boolean {
        return try {
            userCollection.document(userId)
                .update("statusRole", Status.REJECTED)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ---------------------------------------------------------
    //  Update User
    // ---------------------------------------------------------
    override suspend fun updateUser(updatedUser: User): Boolean {
        return try {
            userCollection.document(updatedUser.uid)
                .set(updatedUser)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ---------------------------------------------------------
    //  Check if CURRENT user is ADMIN
    // ---------------------------------------------------------
    override suspend fun isCurrentUserAdmin(): Boolean {
        val user = getCurrentUser() ?: return false
        return user.role == Roles.ADMIN
    }

    // ---------------------------------------------------------
    //  Check if a specific user is a VERIFIED doctor
    // ---------------------------------------------------------
    override suspend fun isCurrentUserDoctorVerified(): Boolean {
        val u = getCurrentUser() ?: return false
        return u.role == Roles.MEDECIN && u.statusRole == Status.VALIDATED
    }

    // ---------------------------------------------------------
    //  Check if a specific user is a VERIFIED coordinator
    // ---------------------------------------------------------
    override suspend fun isCurrentUserCoordinatorVerified(): Boolean {
        val u = getCurrentUser() ?: return false
       return u.role == Roles.COORDINATEUR && u.statusRole == Status.VALIDATED
    }
}
