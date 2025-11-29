package com.example.healthconnect.ui.missiondetail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.healthconnect.data.models.*
import com.example.healthconnect.navigation.AppRoutes
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailView(
    missionId: String,
    navController: NavController,
    viewModel: MissionDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val mission by viewModel.mission.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val activeUserRole by viewModel.activeUserRole.collectAsState() // MODIFIÉ
    val error by viewModel.error.collectAsState()
    val isDeleted by viewModel.isDeleted.collectAsState()
    val userParticipations by viewModel.userParticipations.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(missionId) {
        viewModel.loadMission(missionId)
    }

    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            Toast.makeText(context, "Mission supprimée avec succès", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {},
        bottomBar = {
            if (mission != null && !isLoading && error == null) {
                MissionBottomBar(
                    mission = mission!!,
                    activeUserRole = activeUserRole, // MODIFIÉ
                    navController = navController,
                    onDeleteClick = { showDeleteDialog = true }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                error != null -> Text(
                    text = error!!,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    textAlign = TextAlign.Center
                )
                mission != null -> {
                    MissionDetailContent(
                        mission = mission!!,
                        activeUserRole = activeUserRole, // MODIFIÉ
                        userParticipations = userParticipations,
                        navController = navController,
                        onApplyClick = { taskId ->
                            viewModel.applyForTask(mission!!.id, taskId)
                        }
                    )
                }
            }

            if (showDeleteDialog && mission != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Confirmer la suppression") },
                    text = { Text("Êtes-vous sûr de vouloir supprimer définitivement la mission \"${mission!!.title}\" ? Cette action est irréversible.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteMission(mission!!.id)
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("Supprimer") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Annuler") }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailContent(
    mission: Mission,
    activeUserRole: Roles?, // MODIFIÉ
    userParticipations: List<Participation>,
    navController: NavController,
    onApplyClick: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                AsyncImage(
                    model = mission.photoMission,
                    contentDescription = mission.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
            }

            Column(
                modifier = Modifier.offset(y = (-24).dp).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.weight(1f, fill = false),
                                text = mission.title ?: "Titre non disponible",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            StatusChip(status = mission.status)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = mission.description ?: "Description non disponible",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }

                Card(elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(16.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoRow(Icons.Default.LocationOn, "Lieu", mission.location ?: "Non spécifié")
                        InfoRow(Icons.Default.DateRange, "Début", formatDate(mission.startDate?.toDate()?.time))
                        InfoRow(Icons.Default.Schedule, "Fin", formatDate(mission.endDate?.toDate()?.time))
                    }
                }

                if (mission.listTasks != null) {
                    TaskDetailsCard(
                        mission = mission,
                        activeUserRole = activeUserRole, // MODIFIÉ
                        userParticipations = userParticipations,
                        onApplyClick = onApplyClick
                    )
                }
            }
        }

        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun MissionBottomBar(
    mission: Mission,
    activeUserRole: Roles?, // MODIFIÉ
    navController: NavController,
    onDeleteClick: () -> Unit
) {
    Surface(shadowElevation = 8.dp) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            when (activeUserRole) {
                Roles.COORDINATEUR -> CoordinatorActionButtons(navController, mission, onDeleteClick)
                Roles.VOLONTAIRE, Roles.MEDECIN -> Spacer(modifier = Modifier.height(0.dp))
                Roles.ADMIN, null -> Spacer(modifier = Modifier.height(0.dp))
            }
        }
    }
}

@Composable
fun TaskDetailsCard(
    mission: Mission,
    activeUserRole: Roles?, // MODIFIÉ
    userParticipations: List<Participation>,
    onApplyClick: (String) -> Unit
) {
    val tasks = mission.listTasks ?: emptyList()

    Card(elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                text = if (activeUserRole == Roles.COORDINATEUR) "Participants & Tâches" else "Postuler pour une Tâche",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            InfoRow(
                icon = Icons.Default.Group,
                label = "Total Inscrits",
                text = mission.participantIds?.size.toString()
            )
            Spacer(modifier = Modifier.height(16.dp))
            ParticipantsRow(
                volunteersCount = mission.nbrVolontaires ?: 0, // Use 0 if null
                doctorsCount = mission.nbrMedecins ?: 0
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            if (tasks.isEmpty()) {
                Text("Aucune tâche spécifique n'a été définie pour cette mission.")
            } else {
                tasks.forEachIndexed { index, task ->
                    TaskInfoItem(
                        task = task,
                        activeUserRole = activeUserRole, // MODIFIÉ
                        userParticipations = userParticipations,
                        onApplyClick = onApplyClick
                    )
                    if (index < tasks.size - 1) {
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TaskInfoItem(
    task: Task,
    activeUserRole: Roles?, // MODIFIÉ
    userParticipations: List<Participation>,
    onApplyClick: (String) -> Unit
) {
    val userParticipationForThisTask = userParticipations.find { it.assignedTaskIds.contains(task.idTask) }
    val isUserApplied = userParticipationForThisTask != null
    val isTaskFull = (task.participantIds?.size ?: 0) >= task.nbrMaxParticipants

    val roleText = if (task.roleType == Roles.MEDECIN) "Médecin(s)" else "Volontaire(s)"
    val icon = if (task.roleType == Roles.MEDECIN) Icons.Default.MedicalServices else Icons.Default.VolunteerActivism
    val progressText = "${task.participantIds?.size ?: 0} / ${task.nbrMaxParticipants}"

    // Logique de candidature précise
    val canApply = !isUserApplied && !isTaskFull && (
            (activeUserRole == Roles.VOLONTAIRE && task.roleType == Roles.VOLONTAIRE) ||
                    (activeUserRole == Roles.MEDECIN && task.roleType == Roles.MEDECIN)
            )
    val showActionRow = activeUserRole == Roles.VOLONTAIRE || activeUserRole == Roles.MEDECIN

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, roleText, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(roleText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))
            Text(progressText, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = task.description ?: "Pas de description.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(start = 32.dp)
        )

        if (showActionRow) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.padding(start = 32.dp)) {
                when {
                    isUserApplied -> {
                        val (statusIcon, statusText, statusColor) = when (userParticipationForThisTask?.validation) {
                            Status.VALIDATED -> Triple(Icons.Default.CheckCircle, "Acceptée", Color(0xFF00C853))
                            Status.REJECTED -> Triple(Icons.Default.Cancel, "Rejetée", MaterialTheme.colorScheme.error)
                            else -> Triple(Icons.Default.HourglassTop, "En attente", Color.Gray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(statusIcon, contentDescription = null, tint = statusColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(statusText, color = statusColor, fontWeight = FontWeight.Bold)
                        }
                    }
                    canApply -> {
                        Button(onClick = { onApplyClick(task.idTask) }) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Postuler")
                        }
                    }
                    isTaskFull -> OutlinedButton(onClick = {}, enabled = false) { Text("Complet") }
                    else -> OutlinedButton(onClick = {}, enabled = false) { Text("Rôle Incompatible") }
                }
            }
        }
    }
}


// --- AUTRES COMPOSANTS (inchangés mais inclus pour la complétude) ---

@Composable
fun CoordinatorActionButtons(navController: NavController, mission: Mission, onDeleteClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { navController.navigate("${AppRoutes.USER_LIST}/${mission.id}") },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Voir les Demandes", fontSize = 16.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = { navController.navigate("${AppRoutes.ADD_MISSION}?missionId=${mission.id}") },
                modifier = Modifier.weight(1f).height(52.dp)
            ) { Text("Modifier") }
            Button(
                onClick = onDeleteClick,
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Supprimer") }
        }
    }
}

@Composable
fun ParticipantsRow(volunteersCount: Int, doctorsCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ParticipantCounter("Volontaires", volunteersCount, Modifier.weight(1f))
        ParticipantCounter("Médecins", doctorsCount, Modifier.weight(1f))
    }
}

@Composable
fun ParticipantCounter(label: String, count: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = count.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            Text(text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StatusChip(status: MissionStatus) {
    val (backgroundColor, textColor) = when (status) {
        MissionStatus.PLANNED -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        MissionStatus.ONGOING -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        MissionStatus.COMPLETED -> Color.LightGray to Color.DarkGray
        MissionStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    val statusText = when (status) {
        MissionStatus.PLANNED -> "Planifiée"
        MissionStatus.ONGOING -> "En cours"
        MissionStatus.COMPLETED -> "Terminée"
        MissionStatus.CANCELLED -> "Annulée"
    }

    Surface(shape = RoundedCornerShape(8.dp), color = backgroundColor) {
        Text(
            text = statusText,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

private fun formatDate(epochMillis: Long?): String {
    if (epochMillis == null || epochMillis <= 0L) return "Non spécifiée"
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH)
    return sdf.format(Date(epochMillis))
}
