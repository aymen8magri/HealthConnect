package com.example.healthconnect.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.healthconnect.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(navController: NavController, isAdmin: Boolean = false) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        if (isAdmin) {
            // Admin: Utilisateurs, Missions, Profil
            NavigationBarItem(
                icon = { Icon(imageVector = Icons.Default.Group, contentDescription = "Utilisateurs") },
                label = { Text("Utilisateurs") },
                selected = currentDestination?.route == AppRoutes.ADMIN_DASHBOARD,
                onClick = {
                    navController.navigate(AppRoutes.ADMIN_DASHBOARD) {
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
                icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profil") },
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
            // Regular user bar (existing items)
            //Home
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home"
                    )
                },
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

            //Chat
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

            //tasks
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

            //missions
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

            //profile
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "profile"
                    )
                },
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