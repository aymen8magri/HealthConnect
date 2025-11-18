package com.example.healthconnect.data.auth.login

import com.google.firebase.auth.AuthResult

/**
 * L'interface pour le repository de connexion.
 * Elle définit le contrat pour les opérations de connexion.
 */
interface LoginRepository {
    /**
     * Connecte un utilisateur avec son email et son mot de passe.
     * @return AuthResult le résultat de l'authentification Firebase.
     * @throws Exception si la connexion échoue (mauvais mot de passe, etc.).
     */
    suspend fun loginUser(email: String, pass: String): AuthResult
}
