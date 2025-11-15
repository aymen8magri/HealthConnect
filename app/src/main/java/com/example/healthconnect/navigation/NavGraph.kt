package com.example.healthconnect.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthconnect.ui.Home.HomeScreen
import com.example.healthconnect.ui.missions.MissionsView  // ← NOUVEAU
import com.example.healthconnect.ui.chatBot.ChatScreen  // ← NOUVEAU
import com.example.healthconnect.ui.Tasks.TachesScreen  // ← NOUVEAU
import com.example.healthconnect.ui.Profile.ProfileScreen
@Composable
fun NavGraph(
    navController: NavHostController, // <-- Acceptez le NavController en paramètre
    modifier: Modifier = Modifier // <-- Acceptez le Modifier en paramètre
) {
    NavHost(
        navController = navController,
        startDestination = "home", // ou votre écran de démarrage
        modifier = modifier // <-- Appliquez le modifier ici
    ) {
        // Définissez vos écrans
        composable("home") { HomeScreen(navController) }
        composable("chat") { ChatScreen(navController) }
        composable("taches") { TachesScreen(navController) }

        // Votre écran Missions
        composable("missions") {
            MissionsView(navController = navController)
        }

        composable("profile") { ProfileScreen(navController) }
    }
}