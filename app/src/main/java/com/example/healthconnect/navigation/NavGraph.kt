package com.example.healthconnect.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.healthconnect.ui.Home.HomeScreen
import com.example.healthconnect.ui.admin.userDetail.UserDetailView
import com.example.healthconnect.ui.admin.userList.UserListView
import com.example.healthconnect.ui.tasks.TachesScreen
import com.example.healthconnect.ui.auth.login.LoginScreen
import com.example.healthconnect.ui.auth.register.RegisterScreen
import com.example.healthconnect.ui.chatBot.ChatScreen
import com.example.healthconnect.ui.missiondetail.MissionDetailView
import com.example.healthconnect.ui.missions.MissionsView // Assurez-vous que c'est le bon import
import com.example.healthconnect.ui.profile.ProfileViewContent
import com.example.healthconnect.ui.missions.AddMissionScreen

// Définissons les routes dans un objet pour éviter les erreurs de frappe
object AppRoutes {
    const val HOME = "home"
    // MODIFICATION: Unifiez la route des missions
    const val MISSIONS = "missions"
    const val MISSION_DETAIL = "mission_detail"
    const val REGISTER ="register"
    const val LOGIN = "login"
    // ... autres routes
    const val USER_LIST = "user_list/{missionId}"
    const val USER_DETAIL = "user_detail"
    const val ADD_MISSION = "add_mission"
    const val PROFILE = "profile"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN,
        modifier = modifier
    ) {
        // --- CORRECTION : Route unifiée pour les missions ---
        composable(
            route = "${AppRoutes.MISSIONS}?showMyMissions={showMyMissions}", // Argument optionnel
            arguments = listOf(navArgument("showMyMissions") {
                type = NavType.BoolType
                defaultValue = false // Par défaut, on montre toutes les missions
            })
        ) { backStackEntry ->
            val showMyMissions = backStackEntry.arguments?.getBoolean("showMyMissions") ?: false
            // Assurez-vous d'utiliser le nom correct de votre Composable ici (ex: MissionsView)
            MissionsView(
                navController = navController,
                showMyMissions = showMyMissions, // Passez l'argument à l'écran
                onMissionClick = { missionId ->
                    navController.navigate("${AppRoutes.MISSION_DETAIL}/$missionId")
                }
            )
        }

        // Add mission screen
        composable(
            route = "${AppRoutes.ADD_MISSION}?missionId={missionId}",
            arguments = listOf(navArgument("missionId") {
                type = NavType.StringType
                nullable = true // L'ID est optionnel (nul pour une nouvelle mission)
            })
        ) { backStackEntry ->
            val missionId = backStackEntry.arguments?.getString("missionId")
            AddMissionScreen(
                navController = navController,
                missionId = missionId // Passez l'ID à l'écran
            )
        }
        // --- NOUVEAU COMPOSABLE POUR L'INSCRIPTION ---
        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(AppRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }

        // Route pour le détail d'une mission
        composable(
            route = "${AppRoutes.MISSION_DETAIL}/{missionId}",
            arguments = listOf(navArgument("missionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val missionId = backStackEntry.arguments?.getString("missionId")
            if (missionId != null) {
                MissionDetailView( missionId = missionId, navController = navController)
            }
        }

        composable(AppRoutes.HOME) { HomeScreen(navController) }
        composable("chat") { ChatScreen(navController) }
        composable("taches") { TachesScreen(navController) }

        composable(AppRoutes.PROFILE) {
            ProfileViewContent(
                navController = navController,
                onLogout = onLogout
            )
        }

        // Admin dashboard -> liste des utilisateurs
        composable(
            "${AppRoutes.USER_LIST}/{missionId}",
            arguments = listOf(navArgument("missionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
            UserListView(
                navController = navController,
                missionId = missionId
            ) { userId ->
                navController.navigate("${AppRoutes.USER_DETAIL}/$userId")
            }
        }

        // Route pour le détail d'un utilisateur
        composable(
            route = "${AppRoutes.USER_DETAIL}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UserDetailView(navController = navController, userId = userId)
            }
        }
    }
}
