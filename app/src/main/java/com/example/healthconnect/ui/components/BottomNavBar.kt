package com.example.healthconnect.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.healthconnect.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    navController: NavController,
    viewModel: BottomNavViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isAdmin = viewModel.isAdmin.value

    // Wait until Firebase loads the role
    if (isAdmin == null) return

    NavigationBar {

        if (isAdmin) {
            // -------------------------
            // ADMIN NAVIGATION
            // -------------------------
            NavigationBarItem(
                icon = { Icon(Icons.Default.Group, contentDescription = "Utilisateurs") },
                label = { Text("Utilisateurs") },
                selected = currentDestination?.route == AppRoutes.USER_LIST,
                onClick = {
                    navController.navigate(AppRoutes.USER_LIST) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Missions") },
                label = { Text("Missions") },
                selected = currentDestination?.route == AppRoutes.MISSIONS,
                onClick = {
                    navController.navigate(AppRoutes.MISSIONS) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                label = { Text("Profil") },
                selected = currentDestination?.route == AppRoutes.PROFILE,
                onClick = {
                    navController.navigate(AppRoutes.PROFILE) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

        } else {
            // -------------------------
            // NORMAL USER NAVIGATION
            // -------------------------
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = currentDestination?.route == "home",
                onClick = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Email, contentDescription = "Chat") },
                label = { Text("Chat") },
                selected = currentDestination?.route == "chat",
                onClick = {
                    navController.navigate("chat") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = "Tâches") },
                label = { Text("Tâches") },
                selected = currentDestination?.route == "taches",
                onClick = {
                    navController.navigate("taches") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Missions") },
                label = { Text("Missions") },
                selected = currentDestination?.route == "missions",
                onClick = {
                    navController.navigate("missions") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profil") },
                selected = currentDestination?.route == "profile",
                onClick = {
                    navController.navigate("profile") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
