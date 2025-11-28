package com.example.healthconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.healthconnect.data.admin.validateUsers.UserRepositoryImpl
import com.example.healthconnect.navigation.NavGraph
import com.example.healthconnect.ui.MainViewModel
import com.example.healthconnect.ui.components.BottomNavBar
import com.example.healthconnect.ui.theme.HealthConnectTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var userRepository: UserRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthConnectTheme {
                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()



                // Observe auth state
                DisposableEffect(auth) {
                    val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        val user = firebaseAuth.currentUser
                        if (user == null) {
                            // User logged out, navigate to login
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }

                        
                        }
                    }
                    auth.addAuthStateListener(authListener)
                    onDispose { auth.removeAuthStateListener(authListener) }
                }

                // Manage bottom bar visibility
                var showBottomBar by remember { mutableStateOf(false) }
                LaunchedEffect(navController) {
                    navController.currentBackStackEntryFlow.collect { backStackEntry ->
                        val route = backStackEntry.destination.route
                        showBottomBar = route != "login" && route != "register"
                    }
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        onLogout = { mainViewModel.onLogoutClicked() }
                    )
                }
            }
        }
    }
}
