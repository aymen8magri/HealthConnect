package com.example.healthconnect.data.admin.validateUsers

import com.example.healthconnect.data.models.Roles
import com.example.healthconnect.data.models.Specialite
import com.example.healthconnect.data.models.Status
import com.example.healthconnect.data.models.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    // Mutable list so we can simulate updates in this in-memory implementation
    private val usersList = mutableListOf(
        User(
            uid = "user_1",
            fullName = "Dr. Amina Ben Ali",
            email = "amina.benali@example.com",
            phoneNumber = "002165551234",
            role = Roles.MEDECIN,
            age = 38,
            adresse = "Rue de la Santé, Tunis",
            bio = "Médecin généraliste avec 10 ans d'expérience.",
            image = "https://picsum.photos/seed/medecin1/200/200",
            statusRole = Status.PENDING,
            cabinet = "Cabinet A",
            specialiteMedecin = Specialite.GENERALISTE,
            carteService = "https://picsum.photos/seed/carte1/400/300"
        ),
        User(
            uid = "user_2",
            fullName = "Sami Trabelsi",
            email = "sami.trabelsi@example.com",
            phoneNumber = "002165559876",
            role = Roles.COORDINATEUR,
            age = 45,
            adresse = "Avenue Habib Bourguiba",
            bio = "Coordinateur d'associations.",
            image = "https://picsum.photos/seed/coord1/200/200",
            statusRole = Status.VALIDATED,
            association = "Association Espoir",
            posteAsso = "Président",
            carteAssociation = "https://picsum.photos/seed/assoc1/400/300"
        ),
        User(
            uid = "user_3",
            fullName = "Leïla Saidi",
            email = "leila.saidi@example.com",
            phoneNumber = "002165551111",
            role = Roles.VOLONTAIRE,
            age = 29,
            adresse = "Rue Centrale",
            bio = "Volontaire enthousiaste.",
            image = "https://picsum.photos/seed/vol1/200/200",
            statusRole = Status.REJECTED
        ),
        User(
            uid = "user_4",
            fullName = "Dr. Mohamed Karim",
            email = "karim.med@example.com",
            phoneNumber = "002165552222",
            role = Roles.MEDECIN,
            age = 42,
            adresse = "Rue Ibn Sina",
            bio = "Cardiologue spécialisé.",
            image = "https://picsum.photos/seed/medecin2/200/200",
            statusRole = Status.VALIDATED,
            cabinet = "Cabinet Médical Central",
            specialiteMedecin = Specialite.CARDIOLOGUE,
            carteService = "https://picsum.photos/seed/carte2/400/300"
        ),
        // Admin user for testing
        User(
            uid = "admin_1",
            fullName = "Admin Test",
            email = "admin@example.com",
            phoneNumber = "0000000000",
            role = Roles.ADMIN,
            age = 30,
            adresse = "Siège",
            bio = "Compte administrateur de test.",
            image = "https://picsum.photos/seed/admin/200/200",
            statusRole = Status.VALIDATED
        )
    )

    // Simulated "logged in" user id (set when user logs in)
    private var currentUserId: String? = "user_1"

    // Setter for the simulated current user (use real auth in production)
    fun setCurrentUser(userId: String?) {
        currentUserId = userId
    }

    // Set current user by matching email (non-suspend helper)
    fun setCurrentUserByEmail(email: String?) {
        if (email == null) {
            currentUserId = null
            return
        }
        val found = usersList.find { it.email.equals(email, ignoreCase = true) }
        currentUserId = found?.uid
    }

    // Returns the current user object if set
    fun getCurrentUser(): User? {
        return currentUserId?.let { id -> usersList.find { it.uid == id } }
    }

    // Non-suspend helper to know if the current user is admin
    fun isCurrentUserAdmin(): Boolean {
        val user = getCurrentUser()
        return user?.role == Roles.ADMIN
    }

    override suspend fun getAllUsers(): List<User> {
        // TODO: Replace with real Firestore query later
        return usersList.toList()
    }

    override suspend fun getUserById(userId: String): User? {
        return usersList.find { it.uid == userId }
    }

    override suspend fun verifyUser(userId: String): Boolean {
        // Simulate verification by setting VALIDATED
        val idx = usersList.indexOfFirst { it.uid == userId }
        if (idx >= 0) {
            val u = usersList[idx]
            usersList[idx] = u.copy(statusRole = Status.VALIDATED)
        }
        return true
    }
    override suspend fun rejectUser(userId: String): Boolean {
        // Simulate verification by setting VALIDATED
        val idx = usersList.indexOfFirst { it.uid == userId }
        if (idx >= 0) {
            val u = usersList[idx]
            usersList[idx] = u.copy(statusRole = Status.REJECTED)
        }
        return true
    }


}