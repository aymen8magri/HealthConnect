package com.example.healthconnect.data.auth.register

import android.app.Activity
import com.example.healthconnect.data.models.Roles
import com.example.healthconnect.data.models.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : RegisterRepository {


    override fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (String) -> Unit,
        onVerificationFailed: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onCodeSent(verificationId)
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                onVerificationFailed(e.message ?: "Une erreur est survenue")
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // CETTE FONCTION N'EST APPELÉE QUE LORS DE LA VÉRIFICATION DU CODE SMS
    override fun getPhoneAuthCredential(verificationId: String, smsCode: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, smsCode)
    }

    // CETTE FONCTION N'EST APPELÉE QUE LORS DE LA VÉRIFICATION DU CODE SMS
    override suspend fun registerUserWithPhoneAndEmail(
        credential: PhoneAuthCredential,
        fullName: String,
        email: String,
        pass: String
    ): AuthResult {
        // ÉTAPE 1: Connecte l'utilisateur avec son numéro de téléphone
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        val user = authResult.user ?: throw IllegalStateException("L'utilisateur est nul.")

        // --- Pour tester, vous pouvez commenter tout ce qui suit si vous voulez ---
        // --- Le simple fait de ne pas le commenter ne créera pas l'utilisateur ---
        // --- tant que vous ne saisirez pas le bon code SMS à l'étape suivante. ---

        // ÉTAPE 2: Lie le compte email/mot de passe
        val emailCredential = EmailAuthProvider.getCredential(email, pass)
        user.linkWithCredential(emailCredential).await()

        // ÉTAPE 3: Met à jour le nom de l'utilisateur
        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(fullName).build()
        user.updateProfile(profileUpdates).await()

        // ÉTAPE 4: Sauvegarde les informations dans Firestore
        val newUser = User(
            uid = user.uid,
            fullName = fullName,
            email = email,
            phoneNumber = user.phoneNumber,
            role = Roles.VOLONTAIRE
        )
        firestore.collection("users").document(user.uid).set(newUser).await()

        return authResult
    }
}
    