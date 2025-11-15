package com.example.healthconnect.ui.missiondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.healthconnect.data.mission.Mission
import com.example.healthconnect.data.mission.MissionStatus

// --- La vue principale (Scaffold) ne change pas beaucoup ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailView(
    missionId: String,
    navController: NavController,
    viewModel: MissionDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(missionId) {
        viewModel.loadMission(missionId)
    }

    val mission by viewModel.mission.collectAsState()

    Scaffold(
        // Rendre la TopAppBar transparente pour un effet plus immersif
        topBar = {
            TopAppBar(
                title = { Text("Détails de la Mission", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Fond transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest // Couleur de fond générale
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (mission == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                mission?.let {
                    // On passe les paddingValues au contenu pour qu'il ne soit pas sous la barre de statut
                    MissionDetailContent(mission = it, modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()))
                }
            }
        }
    }
}

// --- Le contenu détaillé, entièrement redessiné ---
@Composable
fun MissionDetailContent(mission: Mission, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // --- 1. EN-TÊTE VISUELLE ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Un peu plus haut
        ) {
            // Image ou initiales en fond
            if (!mission.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = mission.imageUrl,
                    contentDescription = mission.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF00897B)), // Couleur unie plus élégante
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mission.title.take(3).uppercase(),
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            // Scrim (dégradé sombre) pour la lisibilité du texte de la TopAppBar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.3f)
                    )
            )
        }

        // --- 2. CONTENU PRINCIPAL AVEC CARTES ---
        Column(
            modifier = Modifier
                .offset(y = (-24).dp) // Fait remonter le contenu pour qu'il chevauche l'image
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CARTE 1: Titre, Description et Statut
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = mission.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        StatusChip(status = mission.status)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mission.description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }

            // CARTE 2: Infos (Lieu, Date, Heure)
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoRow(icon = Icons.Default.LocationOn, label = "Lieu", text = mission.location)
                    InfoRow(icon = Icons.Default.DateRange, label = "Date", text = mission.date)
                    InfoRow(icon = Icons.Default.Schedule, label = "Heures", text = "${mission.startTime} - ${mission.endTime}")
                }
            }

            // CARTE 3: Matériel Requis
            if (mission.materialRequired.isNotEmpty()) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Matériel Requis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        mission.materialRequired.forEach { material ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(material, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            // CARTE 4: Participants
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Participants", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    ParticipantsRow(
                        volunteersCount = mission.volunteers.size,
                        maxVolunteers = mission.maxVolunteers,
                        doctorsCount = mission.doctors.size,
                        maxDoctors = mission.maxDoctors
                    )
                }
            }
        }

        // --- 3. BOUTON D'ACTION EN BAS ---
        // Le `Spacer` pousse le bouton en bas de l'écran s'il y a peu de contenu
        if (rememberScrollState().canScrollForward || rememberScrollState().canScrollBackward) {
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            val isButtonEnabled = mission.status == MissionStatus.PLANNED || mission.status == MissionStatus.IN_PROGRESS
            val buttonText = when (mission.status) {
                MissionStatus.PLANNED, MissionStatus.IN_PROGRESS -> "Rejoindre la Mission"
                MissionStatus.COMPLETED -> "Mission Terminée"
                MissionStatus.CANCELLED -> "Mission Annulée"
            }

            Button(
                onClick = { /* TODO: Logique pour rejoindre la mission */ },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(buttonText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// --- Les composants réutilisables, légèrement redessinés ---

@Composable
fun InfoRow(icon: ImageVector, label: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

// ParticipantsRow, ParticipantCounter, et StatusChip ne changent pas structurellement
// mais bénéficieront du nouveau design grâce aux `Card`

@Composable
fun ParticipantsRow(volunteersCount: Int, maxVolunteers: Int, doctorsCount: Int, maxDoctors: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ParticipantCounter(
            modifier = Modifier.weight(1f),
            label = "Volontaires",
            count = volunteersCount,
            max = maxVolunteers,
            color = MaterialTheme.colorScheme.tertiary
        )
        ParticipantCounter(
            modifier = Modifier.weight(1f),
            label = "Docteurs",
            count = doctorsCount,
            max = maxDoctors,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ParticipantCounter(modifier: Modifier = Modifier, label: String, count: Int, max: Int, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = "$count/$max",
                color = color,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun StatusChip(status: MissionStatus) {
    val (backgroundColor, textColor) = when (status) {
        MissionStatus.PLANNED -> Color(0xFFE6FFFA) to Color(0xFF38A169)
        MissionStatus.IN_PROGRESS -> Color(0xFFEBF4FF) to Color(0xFF3182CE)
        MissionStatus.COMPLETED -> Color(0xFFF7FAFC) to Color(0xFF718096)
        MissionStatus.CANCELLED -> Color(0xFFFFF5F5) to Color(0xFFE53E3E)
    }
    val statusText = when (status) {
        MissionStatus.PLANNED -> "Planifiée"
        MissionStatus.IN_PROGRESS -> "En cours"
        MissionStatus.COMPLETED -> "Terminée"
        MissionStatus.CANCELLED -> "Annulée"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = statusText,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
