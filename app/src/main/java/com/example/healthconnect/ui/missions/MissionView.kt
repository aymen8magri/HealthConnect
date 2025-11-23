package com.example.healthconnect.ui.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.Badge
import androidx.compose.runtime.*
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.healthconnect.data.models.Mission
import com.example.healthconnect.data.models.MissionStatus
import java.text.SimpleDateFormat
import java.util.*

// --- La vue principale, avec une nouvelle couleur de fond ---
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun MissionsView(
    navController: NavHostController,
    viewModel: MissionViewModel = hiltViewModel(),
    onMissionClick: (String) -> Unit
) {
    val missions by viewModel.filteredMissions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.statusFilter.collectAsState()
    val (showFilterMenu, setShowFilterMenu) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MissionsTopBar(missionCount = missions.size)
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // BARRE DE RECHERCHE + FILTRE (toujours visible)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF00897B))
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onFilterClick = { setShowFilterMenu(true) },
                    selectedStatus = selectedStatus,
                    showMenu = showFilterMenu,
                    onDismissMenu = { setShowFilterMenu(false) },
                    onStatusSelected = { status ->
                        viewModel.onStatusFilterChange(status)
                        setShowFilterMenu(false)
                    }
                )
            }

            // --- MESSAGE SI AUCUNE MISSION TROUVÉE ---
            if (missions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune mission trouvée.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // --- LISTE DES MISSIONS ---
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(missions) { mission ->
                        MissionCard(
                            mission = mission,
                            onClick = { onMissionClick(mission.id) }
                        )
                    }
                }
            }
        }
    }

}

// --- La TopAppBar, avec le nouveau style de compteur ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsTopBar(missionCount: Int) {
    TopAppBar(
        title = {
            Text(
                text = "Mission",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00796B)
                )
            )
        },
        actions = {
            Button(
                onClick = { },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2F1)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Missions trouvées", color = Color(0xFF00796B)) // Texte avec la couleur sarcelle
                    Spacer(Modifier.width(8.dp)) // Espace entre le texte et le badge
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) {
                        Text(text = missionCount.toString(),
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    )
}





// --- La barre de recherche, avec un design plus moderne ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    selectedStatus: MissionStatus?,
    showMenu: Boolean,
    onDismissMenu: () -> Unit,
    onStatusSelected: (MissionStatus?) -> Unit
) {
    Box {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Rechercher des missions...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Icône de recherche", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            trailingIcon = {
                Box {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Icône de filtre",
                        modifier = Modifier
                            .clickable { onFilterClick() }
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (selectedStatus != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd) // Aligne le badge au coin supérieur droit de l'icône Tune
                                .size(14.dp) // Taille du cercle du badge
                                .background(
                                    color = MaterialTheme.colorScheme.error,
                                    shape = CircleShape
                                )
                                .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape), // Bordure pour séparer
                            contentAlignment = Alignment.Center // Centre l'icône de coche à l'intérieur
                        ) {
                            // 3. L'icône de coche à l'intérieur du badge
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Filtre actif",
                                modifier = Modifier.size(10.dp),
                                tint = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = onDismissMenu,
        ) {
            // ... (le reste du DropdownMenu ne change pas)
            DropdownMenuItem(
                text = { Text("Tous") },
                onClick = { onStatusSelected(null) },
                leadingIcon = {
                    if (selectedStatus == null) {
                        Icon(Icons.Default.Check, "Sélectionné")
                    }
                }
            )
            MissionStatus.entries.forEach { status ->
                DropdownMenuItem(
                    text = {
                        val statusText = when (status) {
                            MissionStatus.PLANNED -> "Planifiée"
                            MissionStatus.ONGOING -> "En cours"
                            MissionStatus.COMPLETED -> "Terminée"
                            MissionStatus.CANCELLED -> "Annulée"
                        }
                        Text(statusText)
                    },
                    onClick = { onStatusSelected(status) },
                    leadingIcon = {
                        if (selectedStatus == status) {
                            Icon(Icons.Default.Check, "Sélectionné")
                        }
                    }
                )
            }
        }
    }
}




// --- Le composant MissionCard, entièrement redessiné ---
@Composable
fun MissionCard(mission: Mission, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Column {
            // --- Section image et statut ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // Image un peu plus grande
                    .background(Color.LightGray)
            ) {
                if (mission.photoMission?.isNotBlank() == true) {
                    AsyncImage(
                        model = mission.photoMission,
                        contentDescription = mission.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF00897B).copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mission.title?.take(3)?.uppercase() ?: "",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Le statut est maintenant un "chip" en superposition sur l'image
                StatusChip(
                    status = mission.status,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                )
            }

            // --- Section informations ---
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = mission.title?.take(3)?.uppercase() ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mission?.description ?: "Non spécifié",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2 // On peut se permettre 2 lignes maintenant
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoChip(
                        text = formatMissionDates(mission.startDate?.toDate()?.time ?: 0L, mission.endDate?.toDate()?.time ?: 0L),
                        icon = Icons.Outlined.DateRange
                    )
                    InfoChip(
                        text = mission?.location ?:"Non spécifié",
                        icon = Icons.Default.LocationOn
                    )
                }
            }
        }
    }
}

fun formatMissionDates(start: Long, end: Long): String {
    return try {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val s = if (start > 0) fmt.format(Date(start)) else "-"
        val e = if (end > 0) fmt.format(Date(end)) else "-"
        if (s == "-" && e == "-") "" else "$s – $e"
    } catch (_: Exception) {
        ""
    }
}

// InfoChip avec un style plus subtil
@Composable
fun InfoChip(
    text: String,
    icon: ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Le bouton de statut n'est plus nécessaire ici, il est remplacé par StatusChip
// Ajout du composant StatusChip qui manquait peut-être dans ce fichier
@Composable
fun StatusChip(status: MissionStatus, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor) = when (status) {
        MissionStatus.PLANNED -> Color(0xFFE6FFFA) to Color(0xFF38A169)
        MissionStatus.ONGOING -> Color(0xFFEBF4FF) to Color(0xFF3182CE)
        MissionStatus.COMPLETED -> Color(0xFFF7FAFC) to Color(0xFF718096)
        MissionStatus.CANCELLED -> Color(0xFFFFF5F5) to Color(0xFFE53E3E)
    }
    val statusText = when (status) {
        MissionStatus.PLANNED -> "Planifiée"
        MissionStatus.ONGOING -> "En cours"
        MissionStatus.COMPLETED -> "Terminée"
        MissionStatus.CANCELLED -> "Annulée"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = statusText,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
