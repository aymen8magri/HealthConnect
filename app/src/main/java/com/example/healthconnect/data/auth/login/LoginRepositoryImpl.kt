package com.example.healthconnect.data.auth.login

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * L'implémentation du LoginRepository.
 * C'est cette classe qui communique directement avec Firebase Authentication.
 */
@Singleton
class LoginRepositoryImpl @Inject constructor(
    // Hilt va injecter l'instance de FirebaseAuth que nous avons définie dans AppModule.
    private val firebaseAuth: FirebaseAuth
) : LoginRepository {

    /**
     * Implémentation de la fonction de connexion.
     */
    override suspend fun loginUser(email: String, pass: String): AuthResult {
        // C'est l'appel direct à l'API Firebase pour connecter l'utilisateur.
        // .await() est une fonction d'extension de la librairie 'kotlinx-coroutines-play-services'
        // qui permet d'utiliser 'suspend' avec les Tâches Firebase.
        return firebaseAuth.signInWithEmailAndPassword(email, pass).await()
    }
}
