package com.example.healthconnect.ui.missions

// ... tous vos imports existants
import androidx.compose.foundation.Image // <-- Import nécessaire
import androidx.compose.foundation.background // <-- Import nécessaire
import androidx.compose.foundation.layout.* // <-- Import nécessaire
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape // <-- Import nécessaire
import androidx.compose.material.icons.Icons // <-- Import nécessaire
import androidx.compose.material.icons.filled.LocationOn // <-- Import nécessaire
import androidx.compose.material3.* // <-- Import nécessaire
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment // <-- Import nécessaire
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // <-- Import nécessaire
import androidx.compose.ui.graphics.Color // <-- Import nécessaire
import androidx.compose.ui.layout.ContentScale // <-- Import nécessaire
import androidx.compose.ui.res.painterResource // <-- Import nécessaire
import androidx.compose.ui.text.font.FontWeight // <-- Import nécessaire
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // <-- Import nécessaire
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.healthconnect.data.mission.Mission
import com.example.healthconnect.data.mission.MissionStatus
import coil.compose.AsyncImage // <-- 1. IMPORTEZ AsyncImage DE COIL
import androidx.compose.material.icons.outlined.DateRange // <-- Import nécessaire
import androidx.compose.material.icons.filled.Schedule // <-- Import nécessaire pour l'horloge
import androidx.compose.ui.graphics.vector.ImageVector // <-- Import nécessaire
@Composable
fun MissionsView(navController: NavHostController,
                 viewModel: MissionViewModel = hiltViewModel()
) {
    val missions by viewModel.missions.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        items(missions) { mission ->
            // Maintenant, cet appel est valide car la fonction est définie plus bas
            MissionCard(mission)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


// --- REMPLACEZ L'ANCIENNE MissionCard PAR CELLE-CI ---
@Composable
fun MissionCard(mission: Mission) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // Fond blanc
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // La carte est maintenant une Ligne (Row)
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- 1. Affiche l'image OU les initiales ---
            if (!mission.imageUrl.isNullOrBlank()) {
                // S'il y a une URL d'image, on affiche l'image
                AsyncImage(
                    model = mission.imageUrl,
                    contentDescription = mission.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)) // Applique les mêmes coins arrondis que la boîte
                )
            } else {
                // Sinon, on affiche la boîte avec les initiales
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFE0F7FA), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mission.title.take(3).uppercase(),
                        color = Color(0xFF00796B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- 2. Colonne avec les détails (droite) ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mission.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = mission.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(mission.location, color = Color.Gray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- 3. Ligne pour la date et l'heure ---
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(
                        text = mission.date,
                        icon = Icons.Outlined.DateRange
                    )
                    // Fournir l'icône d'horloge
                    InfoChip(
                        text = "${mission.startTime} - ${mission.endTime}",
                        icon = Icons.Filled.Schedule
                    )
                }
            }
        }

        // --- 4. Bouton de statut en bas ---
        MissionStatusButton(mission.status)
    }
}


/**
 * Un composant pour afficher une information (comme la date ou l'heure)
 * avec une icône optionnelle.
 */
@Composable
fun InfoChip(
    text: String,
    icon: ImageVector // Le paramètre pour l'icône
) {
    Row(
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically // Pour aligner l'icône et le texte
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // L'icône est décorative
            modifier = Modifier.size(14.dp), // Taille plus petite pour l'icône
            tint = Color.DarkGray
        )
        Spacer(modifier = Modifier.width(4.dp)) // Espace entre l'icône et le texte
        Text(text = text, fontSize = 12.sp, color = Color.DarkGray)
    }
}


/**
 * Le bouton de statut qui prend toute la largeur en bas de la carte.
 */
@Composable
fun MissionStatusButton(status: MissionStatus) {
    val (backgroundColor, textColor) = when (status) {
        MissionStatus.PLANNED -> Color(0xFFE6FFFA) to Color(0xFF38A169) // Vert clair / Vert foncé
        MissionStatus.IN_PROGRESS -> Color(0xFFEBF4FF) to Color(0xFF3182CE) // Bleu clair / Bleu foncé
        MissionStatus.COMPLETED -> Color(0xFFF7FAFC) to Color(0xFF718096) // Gris clair / Gris foncé
        MissionStatus.CANCELLED -> Color(0xFFFFF5F5) to Color(0xFFE53E3E) // Rouge clair / Rouge foncé
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.name.replace("_", " ").uppercase(), // ex: "IN PROGRESS"
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
