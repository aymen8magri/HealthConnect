package com.example.healthconnect.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ProfileView(
    // Le NavController n'est plus nécessaire ici si on ne gère plus la navigation
    // mais on le garde pour d'éventuelles navigations futures depuis le profil.
    navController: NavController,
    onLogout: () -> Unit // On reçoit une fonction à exécuter lors du clic
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Écran de Profil")
        Button(onClick = onLogout) { // On appelle directement la fonction reçue
            Text("Se déconnecter")
        }
    }
}
