package com.example.healthconnect.ui.auth.register

import android.app.Activity
import com.example.healthconnect.data.auth.register.RegisterRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) {
    // Cette fonction démarre la vérification du numéro
    fun startPhoneNumberVerification(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (String) -> Unit,
        onVerificationFailed: (String) -> Unit
    ) {
        registerRepository.sendVerificationCode(
            phoneNumber = phoneNumber,
            activity = activity,
            onCodeSent = onCodeSent,
            onVerificationFailed = onVerificationFailed
        )
    }

    //  une fonction pour finaliser l'inscription avec le code SMS
    suspend fun finalizeRegistration(
        verificationId: String,
        smsCode: String,
        fullName: String,
        email: String,
        pass: String
    ) {
        // 1. Crée le "credential" à partir du code SMS
        val credential = registerRepository.getPhoneAuthCredential(verificationId, smsCode)

        // 2. Finalise l'inscription complète (téléphone + email + Firestore)
        registerRepository.registerUserWithPhoneAndEmail(credential, fullName, email, pass)
    }
}
