package com.example.healthconnect.ui.Tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.util.Locale

// --- Enum pour les statuts des tâches ---
enum class TacheStatus(val displayName: String) {
    COMPLETED("Terminée"),
    SCHEDULED("Planifiée")
}

data class Tache(
    val id: Int,
    val title: String,
    val subtitle: String,
    val doctor: String,
    val date: String,
    val status: TacheStatus
)

// --- L'écran des tâches avec le design corrigé ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TachesScreen(navController: NavHostController) {
    // --- Données et États ---
    val allTaches = remember {
        listOf(
            Tache(1, "Bilan sanguin complet", "Résultats de laboratoire", "Dr. Sarah Johnson", "10 Oct, 2025", TacheStatus.COMPLETED),
            Tache(2, "Visite annuelle", "Consultation", "Dr. Sarah Johnson", "10 Oct, 2025", TacheStatus.SCHEDULED),
            Tache(3, "Rendez-vous cardiologue", "Suivi", "Dr. Marc Dubois", "22 Nov, 2025", TacheStatus.SCHEDULED)
        )
    }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<TacheStatus?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }

    // --- Logique de filtrage ---
    val filteredTaches = remember(searchQuery, selectedStatus) {
        allTaches.filter { tache ->
            val matchesSearch = searchQuery.isBlank() || tache.title.contains(searchQuery, true) || tache.doctor.contains(searchQuery, true)
            val matchesStatus = selectedStatus == null || tache.status == selectedStatus
            matchesSearch && matchesStatus
        }
    }

    Scaffold(
        topBar = {
            // Utilisation de la TopAppBar avec le style de MissionView
            TachesTopBar(tacheCount = filteredTaches.size)
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // --- Barre de recherche avec le même fond que MissionView ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF00897B)) // <-- COULEUR DE FOND IDENTIQUE
                    .padding(16.dp)
            ) {
                TachesSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onFilterClick = { showFilterMenu = true },
                    selectedStatus = selectedStatus,
                    showMenu = showFilterMenu,
                    onDismissMenu = { showFilterMenu = false },
                    onStatusSelected = { status ->
                        selectedStatus = status
                        showFilterMenu = false
                    }
                )
            }

            // --- Liste des tâches ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredTaches) { tache ->
                    TacheItem(tache)
                }
            }
        }
    }
}

// --- TopAppBar pour les Tâches (STYLE IDENTIQUE à MissionView) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TachesTopBar(tacheCount: Int) {
    TopAppBar(
        title = {
            Text(
                text = "Tâches", // Titre adapté
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00796B)
                )
            )
        },
        actions = {
            Button(
                onClick = { /* TODO */ },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2F1)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tâches trouvées", color = Color(0xFF00796B)) // Texte adapté
                    Spacer(Modifier.width(8.dp))
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = tacheCount.toString(),
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

// --- SearchBar pour les Tâches (STYLE IDENTIQUE à MissionView) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TachesSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    selectedStatus: TacheStatus?,
    showMenu: Boolean,
    onDismissMenu: () -> Unit,
    onStatusSelected: (TacheStatus?) -> Unit
) {
    Box {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Rechercher une tâche...") }, // Texte adapté
            leadingIcon = { Icon(Icons.Default.Search, "Recherche", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = {
                Box {
                    Icon(Icons.Default.Tune, "Filtre", Modifier.clickable { onFilterClick() }.padding(8.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (selectedStatus != null) {
                        Box(
                            modifier = Modifier.align(Alignment.TopEnd).size(14.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, "Filtre actif", modifier = Modifier.size(10.dp), tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            // Les couleurs de la barre de recherche sont identiques
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        DropdownMenu(expanded = showMenu, onDismissRequest = onDismissMenu) {
            DropdownMenuItem(
                text = { Text("Tous les statuts") },
                onClick = { onStatusSelected(null) },
                leadingIcon = { if (selectedStatus == null) Icon(Icons.Default.Check, "Sélectionné") }
            )
            TacheStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.displayName) },
                    onClick = { onStatusSelected(status) },
                    leadingIcon = { if (selectedStatus == status) Icon(Icons.Default.Check, "Sélectionné") }
                )
            }
        }
    }
}


// --- Composants TacheItem et InfoChip (inchangés) ---

@Composable
fun TacheItem(tache: Tache) {
    val (statusColor, statusTextColor) = when (tache.status) {
        TacheStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        TacheStatus.SCHEDULED -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(tache.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(tache.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(modifier = Modifier.padding(start = 8.dp).clip(RoundedCornerShape(8.dp)).background(statusColor).padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Text(
                        text = tache.status.displayName.uppercase(Locale.getDefault()),
                        color = statusTextColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoChip(icon = Icons.Default.Person, text = tache.doctor)
                InfoChip(icon = Icons.Default.CalendarToday, text = tache.date)
            }
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
