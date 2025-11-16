package com.example.healthconnect.ui.Profile.logout

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

/**     * Cas d'utilisation pour gérer la déconnexion de l'utilisateur.
 */
class LogoutUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    /**
     * Exécute la déconnexion de l'utilisateur actuel de Firebase.
     */
    fun execute() {
        firebaseAuth.signOut()
    }
}