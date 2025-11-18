package com.example.healthconnect.ui.auth.register

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


import androidx.compose.ui.semantics.error
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    // 1. Recevoir le ViewModel injecté par Hilt
    viewModel: RegisterViewModel = hiltViewModel()
) {
    // 1. Récupérer le contexte actuel
    val context = LocalContext.current
    // 2. Tenter de le caster en Activity (de manière sûre)
    val activity = context as? Activity

    // Les variables d'état pour les champs de texte
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var FullName by remember {mutableStateOf("")}
    var phoneNumber by remember { mutableStateOf("") }
    // Observez l'état du ViewModel pour gérer le chargement, les erreurs, etc.
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            // L'inscription est terminée, on navigue vers l'écran d'accueil
            // et on efface la pile de navigation pour que l'utilisateur ne puisse pas revenir en arrière.
            navController.navigate("home") { // Assurez-vous que "home" est la bonne route dans votre NavGraph
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Si le code n'est pas encore envoyé, on affiche le formulaire d'inscription
            if (!uiState.isVerificationCodeSent) {
                // --- FORMULAIRE D'INSCRIPTION COMPLET (votre code actuel) ---
                Text(
                    text = "Créer un compte",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Rejoignez-nous dès aujourd'hui",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                // Champ FullName
                OutlinedTextField(
                    value = FullName,
                    onValueChange = { FullName = it },
                    label = { Text("Nom complet") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Champ Numéro de téléphone
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Numéro de téléphone (ex: +216... )") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Champ Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Champ Mot de passe
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Champ Confirmer Mot de passe
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmer le mot de passe") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Bouton d'inscription
                Button(
                    onClick = {
                        viewModel.onRegisterClicked(
                            fullName = FullName,
                            phoneNumber = phoneNumber, // Format +216... est crucial
                            email = email,
                            pass = password,
                            confirmPass = confirmPassword,
                            activity = activity
                        )
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = "S'inscrire", fontSize = 16.sp)
                    }
                }
                // --- FIN DU FORMULAIRE D'INSCRIPTION ---

            } else {
                // --- NOUVEAU BLOC : FORMULAIRE DE VÉRIFICATION DU CODE SMS ---
                var smsCode by remember { mutableStateOf("") }

                Text(
                    text = "Vérification",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Veuillez saisir le code à 6 chiffres envoyé au $phoneNumber",
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                OutlinedTextField(
                    value = smsCode,
                    onValueChange = { smsCode = it },
                    label = { Text("Code de vérification") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.onVerificationCodeEntered(smsCode) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Vérifier et S'inscrire")
                    }
                }
            }

            // --- PARTIE COMMUNE (Erreurs et lien de connexion) ---
            // Affichez l'erreur dans tous les cas
            uiState.error?.let { errorMessage ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lien vers la connexion
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Vous avez déjà un compte ?")
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Connectez-vous", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

        }
}}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}
