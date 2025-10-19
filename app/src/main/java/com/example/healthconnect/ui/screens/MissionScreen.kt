package com.example.healthconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.healthconnect.ui.components.BottomNavBar

data class Mission(val id: Int, val title: String, val description: String, val goal: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsScreen(navController: NavHostController) {
    val missions = listOf(
        Mission(1, "Marche quotidienne", "Fais une promenade", "10 000 pas"),
        Mission(2, "Méditation", "Relax-toi un peu", "10 minutes"),
        Mission(3, "Hydratation", "Bois de l'eau", "2 litres")
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(missions) { mission ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(mission.title, style = MaterialTheme.typography.titleMedium)
                            Text(mission.description, style = MaterialTheme.typography.bodyMedium)
                            Text("Objectif: ${mission.goal}", style = MaterialTheme.typography.bodySmall)
                        }
                        Button(
                            onClick = { /* TODO: Logique pour rejoindre la mission */ },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Rejoindre")
                        }
                    }
                }
            }
        }
    }
}