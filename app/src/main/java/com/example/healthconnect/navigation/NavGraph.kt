package com.example.healthconnect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthconnect.ui.Home.HomeScreen
import com.example.healthconnect.ui.missions.MissionsScreen  // ← NOUVEAU
import com.example.healthconnect.ui.chatBot.ChatScreen  // ← NOUVEAU
import com.example.healthconnect.ui.Tasks.TachesScreen  // ← NOUVEAU
import com.example.healthconnect.ui.Profile.ProfileScreen

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