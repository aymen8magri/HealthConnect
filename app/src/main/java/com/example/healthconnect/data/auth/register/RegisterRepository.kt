package com.example.healthconnect.data.auth.register

import android.app.Activity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential

interface RegisterRepository {
    // Ajout de cette fonction pour la vérification du téléphone
    fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (String) -> Unit, // Callback si le code est envoyé (renvoie l'ID de vérification)
        onVerificationFailed: (String) -> Unit // Callback en cas d'erreur
    )

    // Crée le "credential" à partir du code SMS
    fun getPhoneAuthCredential(verificationId: String, smsCode: String): PhoneAuthCredential

    // Finalise l'inscription complète
    suspend fun registerUserWithPhoneAndEmail(
        credential: PhoneAuthCredential,
        fullName: String,
        email: String,
        pass: String
    ): AuthResult
}
    