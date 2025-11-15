package com.example.healthconnect.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.healthconnect.ui.Home.HomeScreen
import com.example.healthconnect.ui.Profile.ProfileScreen
import com.example.healthconnect.ui.Tasks.TachesScreen
import com.example.healthconnect.ui.chatBot.ChatScreen
import com.example.healthconnect.ui.missiondetail.MissionDetailView // <-- Importez le nouvel écran
import com.example.healthconnect.ui.missions.MissionsView

// Définissons les routes dans un objet pour éviter les erreurs de frappe
object AppRoutes {
    const val HOME = "home"
    const val MISSIONS = "missions"
    const val MISSION_DETAIL = "mission_detail"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME, // On démarre sur home
        modifier = modifier
    ) {
        // Route pour la liste des missions
        composable(AppRoutes.MISSIONS) {
            MissionsView(
                navController = navController,
                // On passe la logique de navigation ici
                onMissionClick = { missionId ->
                    navController.navigate("${AppRoutes.MISSION_DETAIL}/$missionId")
                }
            )
        }

        // Route pour le détail d'une mission
        composable(
            route = "${AppRoutes.MISSION_DETAIL}/{missionId}",
            arguments = listOf(navArgument("missionId") { type = NavType.StringType })
        ) { backStackEntry ->
            // On récupère l'ID depuis les arguments de la navigation
            val missionId = backStackEntry.arguments?.getString("missionId")
            if (missionId != null) {
                MissionDetailView(missionId = missionId, navController = navController)
            }
        }

        composable("home") { HomeScreen(navController) }
        composable("chat") { ChatScreen(navController) }
        composable("taches") { TachesScreen(navController) }
        composable("profile") { ProfileScreen(navController) }    }
}
