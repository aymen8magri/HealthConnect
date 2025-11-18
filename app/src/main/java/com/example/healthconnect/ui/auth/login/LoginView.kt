package com.example.healthconnect.ui.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    // CORRECTION : On utilise hiltViewModel() pour que Hilt injecte la bonne instance.
    viewModel: LoginViewModel = hiltViewModel()
) {
    // Les variables d'état pour les champs de texte
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // On observe l'état (LoginState) exposé par le ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Outils pour afficher la Snackbar en cas d'erreur
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Gestion des "effets de bord" : navigation en cas de succès, affichage d'erreur
    LaunchedEffect(key1 = uiState) {
        // En cas de succès de connexion
        if (uiState.isLoginSuccessful) {
            // Navigue vers l'écran d'accueil et nettoie la pile pour empêcher le retour
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }

        // En cas d'erreur
        uiState.error?.let { errorMessage ->
            scope.launch {
                snackbarHostState.showSnackbar(errorMessage)
                // On notifie le ViewModel que l'erreur a été affichée
                viewModel.onErrorMessageShown()
            }
        }
    }

    // Le Scaffold fournit une structure de base et un emplacement pour la Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Titre
            Text(
                text = "Bienvenue !",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Connectez-vous pour continuer",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Champ Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.error != null // Le champ est surligné en rouge en cas d'erreur
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
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.error != null
            )

            TextButton(
                onClick = { /* TODO: Gérer le clic mot de passe oublié */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Mot de passe oublié ?")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bouton de connexion
            Button(
                // CORRECTION : On appelle la fonction du ViewModel lors du clic
                onClick = { viewModel.onLoginClicked(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading // Le bouton est désactivé pendant le chargement
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = "Se connecter", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lien vers l'inscription
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Vous n'avez pas de compte ?")
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Inscrivez-vous", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Pour la preview, on ne peut pas utiliser Hilt, donc on passe un NavController factice
    LoginScreen(navController = rememberNavController())
}
