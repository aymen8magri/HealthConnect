package com.example.healthconnect.ui.auth.register

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.ui.auth.register.RegisterUseCase // Assurez-vous que l'import est correct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isVerificationCodeSent: Boolean = false,
    val isRegistrationSuccessful: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    private var verificationId: String? = null
    // Stocker les infos pour la création finale
    private var fullName: String = ""
    private var email: String = ""
    private var pass: String = ""

    fun onRegisterClicked(
        fullName: String,
        phoneNumber: String,
        email: String,
        pass: String,
        confirmPass: String,
        activity: Activity?
    ) {
        // --- VALIDATIONS ---
        if (activity == null) {
            _uiState.value = RegisterState(error = "Erreur technique, impossible de vérifier le numéro.")
            return
        }
        if (fullName.isBlank() || phoneNumber.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.value = RegisterState(error = "Tous les champs sont obligatoires.")
            return
        }
        if (pass.length < 6) {
            _uiState.value = RegisterState(error = "Le mot de passe doit contenir au moins 6 caractères.")
            return
        }

        if (pass != confirmPass) {
            _uiState.value = RegisterState(error = "Les mots de passe ne correspondent pas.")
            return
        }

        _uiState.value = RegisterState(isLoading = true)

        // --- Lancement de la logique métier ---
        viewModelScope.launch {
            Log.d("REGISTER_DEBUG", "3. Entrée dans viewModelScope.launch.")
            try {
                this@RegisterViewModel.fullName = fullName
                this@RegisterViewModel.email = email
                this@RegisterViewModel.pass = pass


                Log.d("REGISTER_DEBUG", "4. Appel de registerUseCase.startPhoneNumberVerification avec le numéro: $phoneNumber")

                registerUseCase.startPhoneNumberVerification(
                    phoneNumber = phoneNumber,
                    activity = activity,

                    onCodeSent = { receivedVerificationId ->
                        Log.d("REGISTER_DEBUG", "5. SUCCÈS - onCodeSent a été appelé.")
                        this@RegisterViewModel.verificationId = receivedVerificationId
                        _uiState.value = RegisterState(isVerificationCodeSent = true)
                    },
                    onVerificationFailed = { error ->
                        Log.e("REGISTER_DEBUG", "6. ÉCHEC - onVerificationFailed: $error")
                        _uiState.value = RegisterState(error = error)
                    }
                )
            } catch (e: Exception) {
                Log.e("REGISTER_DEBUG", "7. EXCEPTION CATCHÉE: ${e.message}")
                _uiState.value = RegisterState(error = e.message ?: "Une erreur inconnue est survenue.")
            }
        }
    }



    fun onVerificationCodeEntered(code: String) {
        // 1. Vérifier que les informations nécessaires sont bien là
        if (verificationId == null) {
            _uiState.value = RegisterState(error = "Erreur technique, veuillez réessayer.")
            return
        }
        if (code.isBlank() || code.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Veuillez entrer un code valide.")
            return
        }

        // 2. Mettre l'état en chargement
        _uiState.value = RegisterState(isLoading = true, isVerificationCodeSent = true) // On garde isVerificationCodeSent à true

        // 3. Lancer la coroutine pour finaliser l'inscription
        viewModelScope.launch {
            try {
                registerUseCase.finalizeRegistration(
                    verificationId = verificationId!!, // On est sûr qu'il n'est pas nul ici
                    smsCode = code,
                    fullName = this@RegisterViewModel.fullName,
                    email = this@RegisterViewModel.email,
                    pass = this@RegisterViewModel.pass
                )

                // 4. Succès ! Mettre à jour l'état pour déclencher la navigation
                _uiState.value = RegisterState(isRegistrationSuccessful = true)

            } catch (e: Exception) {
                // 5. Échec : Afficher l'erreur
                // (ex: code incorrect, utilisateur déjà existant, etc.)
                _uiState.value = RegisterState(
                    isVerificationCodeSent = true, // On reste sur l'écran du code
                    error = e.message ?: "La vérification du code a échoué."
                )
            }
        }
    }

}
