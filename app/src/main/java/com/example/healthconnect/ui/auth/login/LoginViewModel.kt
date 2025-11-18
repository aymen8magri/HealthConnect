package com.example.healthconnect.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.ui.auth.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. Définir l'état de l'UI pour l'écran de connexion
data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)

// 2. Indiquer à Hilt que cette classe est un ViewModel
@HiltViewModel
class LoginViewModel @Inject constructor(
    // 3. Injecter le UseCase que nous avons créé précédemment
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    // 4. Créer le StateFlow pour exposer l'état à la vue
    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    /**
     * Cette fonction est appelée lorsque l'utilisateur clique sur le bouton "Se connecter".
     */
    fun onLoginClicked(email: String, pass: String) {
        // --- Validations initiales ---
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = LoginState(error = "L'email et le mot de passe ne peuvent pas être vides.")
            return
        }

        // --- Début du processus de connexion ---
        _uiState.value = LoginState(isLoading = true)

        viewModelScope.launch {
            try {
                // Appel du UseCase qui appelle le Repository pour se connecter avec Firebase
                loginUseCase.execute(email, pass)

                // Succès !
                _uiState.value = LoginState(isLoginSuccessful = true)

            } catch (e: Exception) {
                // Échec : Gérer les erreurs (mot de passe incorrect, utilisateur non trouvé, etc.)
                _uiState.value = LoginState(error = e.message ?: "Une erreur de connexion est survenue.")
            }
        }
    }

    /**
     * Fonction pour réinitialiser le message d'erreur une fois qu'il a été affiché.
     */
    fun onErrorMessageShown() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
