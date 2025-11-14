package com.example.healthconnect.ui.Tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.healthconnect.ui.components.BottomNavBar

data class Tache(
    val id: Int,
    val title: String,
    val subtitle: String,
    val doctor: String,
    val date: String,
    val status: String // "Completed" ou "Scheduled"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TachesScreen(navController: NavHostController) {
    val taches = remember {
        listOf(
            Tache(1, "Blood Test - Complete Panel", "Lab Results", "Dr. Sarah Johnson", "Oct 10, 2025", "Completed"),
            Tache(2, "Annual Check-up", "Consultation", "Dr. Sarah Johnson", "Oct 10, 2025", "Scheduled")
        )
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF7F7F7))
        ) {
            // --- Titre et bouton All Records ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "List of taches",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B)
                    )
                )
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2F1)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("All Records", color = Color(0xFF00796B))
                    Spacer(Modifier.width(6.dp))
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        modifier = Modifier.size(18.dp)
                    ) {
                        Text("2", fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            // --- Liste des tâches ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(taches) { tache ->
                    TacheItem(tache)
                }
            }
        }
    }
}

@Composable
fun TacheItem(tache: Tache) {
    val statusColor = when (tache.status) {
        "Completed" -> Color(0xFFD9F6E5)
        "Scheduled" -> Color(0xFFDCE8FF)
        else -> Color.LightGray
    }

    val statusTextColor = when (tache.status) {
        "Completed" -> Color(0xFF0B8A4D)
        "Scheduled" -> Color(0xFF2E5AAC)
        else -> Color.DarkGray
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tache.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = tache.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "👩‍⚕️ ${tache.doctor}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📅 ${tache.date}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .background(statusColor, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tache.status,
                        color = statusTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
