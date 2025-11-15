package com.example.healthconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding // <-- Import nécessaire
import androidx.compose.material3.Scaffold // <-- Import nécessaire
import androidx.compose.ui.Modifier // <-- Import nécessaire
import androidx.navigation.compose.rememberNavController // <-- Import nécessaire
import com.example.healthconnect.navigation.NavGraph
import com.example.healthconnect.ui.components.BottomNavBar // <-- Import nécessaire
import com.example.healthconnect.ui.theme.HealthConnectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthConnectTheme {
                // 1. Créez un contrôleur de navigation ici
                val navController = rememberNavController()

                // 2. Utilisez un Scaffold comme conteneur principal
                Scaffold(
                    // 3. Placez votre BottomNavBar dans le paramètre 'bottomBar'
                    bottomBar = { BottomNavBar(navController = navController) }
                ) { innerPadding ->
                    // 4. Placez votre NavHost (via NavGraph) comme contenu principal
                    //    et appliquez le padding fourni par le Scaffold
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding) // Applique le padding
                    )
                }
            }
        }
    }
}
