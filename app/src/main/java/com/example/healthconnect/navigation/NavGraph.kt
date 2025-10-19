package com.example.healthconnect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthconnect.ui.screens.HomeScreen
import com.example.healthconnect.ui.screens.MissionsScreen  // ← NOUVEAU
import com.example.healthconnect.ui.screens.ChatScreen  // ← NOUVEAU
import com.example.healthconnect.ui.screens.TachesScreen  // ← NOUVEAU
import com.example.healthconnect.ui.screens.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("chat") { ChatScreen(navController) }  // ← NOUVEAU
        composable("taches") { TachesScreen(navController) }  // ← NOUVEAU
        composable("missions") { MissionsScreen(navController) }  // ← NOUVEAU
        composable("profile") { ProfileScreen(navController) }
    }
}