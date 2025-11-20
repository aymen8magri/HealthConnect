package com.example.healthconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.healthconnect.navigation.NavGraph
import com.example.healthconnect.ui.components.BottomNavBar
import com.example.healthconnect.ui.MainViewModel
import com.example.healthconnect.ui.theme.HealthConnectTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import com.example.healthconnect.data.admin.validateUsers.UserRepositoryImpl
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Injection du ViewModel au niveau de l'activité
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var userRepository: UserRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthConnectTheme {
                val navController = rememberNavController()

                // ---- OBSERVATION FIABLE DE L'ÉTAT D'AUTHENTIFICATION ----
                val auth = FirebaseAuth.getInstance()

                // État local indiquant si l'utilisateur connecté est admin
                var isAdmin by remember { mutableStateOf(false) }

                // DisposableEffect gère l'ajout et le retrait de l'écouteur
                DisposableEffect(auth) {
                    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        val user = firebaseAuth.currentUser
                        if (user == null) {
                            // L'utilisateur est déconnecté, rediriger vers login
                            navController.navigate("login") {
                                // Nettoie toute la pile de navigation
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                            // Clear admin flag et l'utilisateur courant
                            isAdmin = false
                            userRepository.setCurrentUser(null)
                        } else {
                            // L'utilisateur est connecté, mapper l'email vers le repo
                            userRepository.setCurrentUserByEmail(user.email)
                            isAdmin = userRepository.isCurrentUserAdmin()
                        }
                    }
                    // Attacher l'écouteur
                    auth.addAuthStateListener(authStateListener)
                    // Détacher l'écouteur lorsque le composable est détruit
                    onDispose {
                        auth.removeAuthStateListener(authStateListener)
                    }
                }

                // ---- Gestion de la barre de navigation (logique existante) ----
                var showBottomBar by remember { mutableStateOf(false) }
                LaunchedEffect(navController) {
                    navController.currentBackStackEntryFlow.collect { backStackEntry ->
                        val currentRoute = backStackEntry.destination.route
                        showBottomBar = when (currentRoute) {
                            "login", "register" -> false
                            else -> true
                        }
                    }
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController, isAdmin = isAdmin)
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        // Passer la fonction de déconnexion au graphe de navigation
                        onLogout = { mainViewModel.onLogoutClicked() }
                    )
                }
            }
        }
    }
}
