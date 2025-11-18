package com.example.healthconnect.ui.auth.login

import com.example.healthconnect.data.auth.login.LoginRepository
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

/**
 * Le UseCase pour la logique de connexion.
 * Il sert de pont entre le ViewModel et le Repository.
 * Cela rend l'architecture plus propre et plus facile à tester.
 */
class LoginUseCase @Inject constructor(
    // Hilt va injecter l'implémentation du LoginRepository (LoginRepositoryImpl) ici.
    private val repository: LoginRepository
) {

    /**
     * Exécute le processus de connexion de l'utilisateur.
     * @param email L'email de l'utilisateur.
     * @param pass Le mot de passe de l'utilisateur.
     * @return AuthResult le résultat de l'authentification Firebase en cas de succès.
     */
    suspend fun execute(email: String, pass: String): AuthResult {
        // Appelle simplement la fonction correspondante du repository.
        // Si une erreur se produit ici (ex: mot de passe incorrect),
        // une exception sera levée et attrapée par le ViewModel.
        return repository.loginUser(email, pass)
    }
}
