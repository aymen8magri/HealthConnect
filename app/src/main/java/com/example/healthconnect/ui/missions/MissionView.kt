package com.example.healthconnect.ui.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
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
import com.example.healthconnect.data.mission.Mission
import com.example.healthconnect.data.mission.MissionStatus

// --- La vue principale, avec une nouvelle couleur de fond ---
@OptIn(ExperimentalMaterial3Api::class)
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
        // Une couleur de fond douce pour tout l'écran
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- La barre de recherche est maintenant sur un fond coloré ---
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

            // --- Liste des missions avec un espacement ---
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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


// --- La TopAppBar, plus minimaliste ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsTopBar(missionCount: Int) {
    TopAppBar(
        title = { Text("Missions", fontWeight = FontWeight.Bold) },
        actions = {
            // Le compteur est maintenant plus subtil
            Text(
                text = "$missionCount trouvées",
                modifier = Modifier.padding(end = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            // Fond de la TopAppBar qui correspond à celui de l'écran
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
                BadgedBox(
                    badge = {
                        if (selectedStatus != null) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary)
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Icône de filtre",
                        modifier = Modifier.clickable { onFilterClick() },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            // Couleurs pour un look plus intégré
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
            DropdownMenuItem(
                text = { Text("Tous") },
                onClick = { onStatusSelected(null) }
            )
            MissionStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = {
                        val statusText = when (status) {
                            MissionStatus.PLANNED -> "Planifiée"
                            MissionStatus.IN_PROGRESS -> "En cours"
                            MissionStatus.COMPLETED -> "Terminée"
                            MissionStatus.CANCELLED -> "Annulée"
                        }
                        Text(statusText)
                    },
                    onClick = { onStatusSelected(status) }
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
                            .background(Color(0xFF00897B).copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mission.title.take(3).uppercase(),
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
                    text = mission.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mission.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2 // On peut se permettre 2 lignes maintenant
                )
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoChip(
                        text = mission.date,
                        icon = Icons.Outlined.DateRange
                    )
                    InfoChip(
                        text = mission.location,
                        icon = Icons.Default.LocationOn
                    )
                }
            }
        }
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
