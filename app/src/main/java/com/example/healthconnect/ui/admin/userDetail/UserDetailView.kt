package com.example.healthconnect.ui.admin.userDetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.healthconnect.R
import com.example.healthconnect.data.models.Roles
import com.example.healthconnect.data.models.Status
import com.example.healthconnect.ui.admin.userDetail.UserDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailView(
    navController: NavHostController,
    userId: String,
    viewModel: UserDetailViewModel = hiltViewModel()
) {
    val userState by viewModel.user.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    var showVerifyDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var zoomedImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Détails utilisateur",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorResource(R.color.white)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = colorResource(R.color.white))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.blue_medium)
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = colorResource(R.color.yellow_light)
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            userState?.let { user ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorResource(R.color.blue_medium))
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(colorResource(R.color.green_teal)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (user.image.isNotBlank()) {
                                    AsyncImage(
                                        model = user.image,
                                        contentDescription = user.fullName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        user.fullName.take(1),
                                        color = colorResource(R.color.white),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 40.sp
                                    )
                                }
                            }

                            // Name and role
                            Text(
                                user.fullName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = colorResource(R.color.white)
                                )
                            )
                            Text(
                                user.role.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    color = colorResource(R.color.yellow_light)
                                )
                            )

                            // Status badge
                            DetailStatusBadge(status = user.status)
                        }
                    }

                    // Content section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Contact card
                        DetailCard {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "Informations de contact",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = colorResource(R.color.blue_dark)
                                    )
                                )

                                ContactRow(
                                    icon = Icons.Filled.Email,
                                    label = "Email",
                                    value = user.email
                                )

                                ContactRow(
                                    icon = Icons.Filled.Call,
                                    label = "Téléphone",
                                    value = user.phoneNumber ?: "Non fourni"
                                )

                                ContactRow(
                                    icon = Icons.Filled.Place,
                                    label = "Adresse",
                                    value = user.adresse.ifBlank { "Non fournie" }
                                )

                                DetailField("Âge", user.age.toString())
                            }
                        }

                        // Bio card
                        DetailCard {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "Biographie",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = colorResource(R.color.blue_dark)
                                    )
                                )
                                Text(
                                    user.bio.ifBlank { "Aucune biographie fournie." },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = colorResource(R.color.blue_dark)
                                    )
                                )
                            }
                        }

                        // Role-specific section
                        when (user.role) {
                            Roles.MEDECIN -> {
                                DetailCard {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            "Informations Médecin",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = colorResource(R.color.blue_dark)
                                            )
                                        )
                                        DetailField("Cabinet", user.cabinet)
                                        DetailField("Spécialité", user.specialiteMedecin.toString())

                                        // Carte de service with zoom
                                        if (user.carteService.isNotBlank()) {
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    "Carte de service",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = colorResource(R.color.blue_medium)
                                                    )
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(150.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(colorResource(R.color.yellow_light))
                                                        .clickable { zoomedImageUrl = user.carteService }
                                                ) {
                                                    AsyncImage(
                                                        model = user.carteService,
                                                        contentDescription = "Carte de service",
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Roles.COORDINATEUR -> {
                                DetailCard {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            "Informations Coordinateur",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = colorResource(R.color.blue_dark)
                                            )
                                        )
                                        DetailField("Association", user.association)
                                        DetailField("Poste", user.posteAsso)

                                        // Carte association with zoom
                                        if (user.carteAssociation.isNotBlank()) {
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    "Carte d'association",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = colorResource(R.color.blue_medium)
                                                    )
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(150.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(colorResource(R.color.yellow_light))
                                                        .clickable { zoomedImageUrl = user.carteAssociation }
                                                ) {
                                                    AsyncImage(
                                                        model = user.carteAssociation,
                                                        contentDescription = "Carte d'association",
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else -> Unit
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showRejectDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    2.dp,
                                    Color(0xFFEF5350)
                                )
                            ) {
                                Text(
                                    "Rejeter",
                                    color = Color(0xFFEF5350),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = { showVerifyDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.green_light)
                                )
                            ) {
                                Text(
                                    "Vérifier",
                                    color = colorResource(R.color.white),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Verify Confirmation Dialog
                if (showVerifyDialog) {
                    AlertDialog(
                        onDismissRequest = { showVerifyDialog = false },
                        title = {
                            Text(
                                "Vérifier l'utilisateur",
                                color = colorResource(R.color.blue_dark),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Text(
                                "Êtes-vous sûr de vouloir vérifier cet utilisateur ?",
                                color = colorResource(R.color.blue_dark)
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showVerifyDialog = false
                                scope.launch {
                                    viewModel.verifyUser(user.uid)
                                    navController.previousBackStackEntry?.savedStateHandle?.set("needsRefresh", true)
                                    navController.popBackStack()
                                }
                            }) {
                                Text("Oui", color = colorResource(R.color.green_light), fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showVerifyDialog = false }) {
                                Text("Annuler", color = colorResource(R.color.blue_medium))
                            }
                        },
                        containerColor = colorResource(R.color.white)
                    )
                }

                // Reject Confirmation Dialog
                if (showRejectDialog) {
                    AlertDialog(
                        onDismissRequest = { showRejectDialog = false },
                        title = {
                            Text(
                                "Rejeter l'utilisateur",
                                color = colorResource(R.color.blue_dark),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Text(
                                "Êtes-vous sûr de vouloir rejeter cet utilisateur ?",
                                color = colorResource(R.color.blue_dark)
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showRejectDialog = false
                                scope.launch {
                                    viewModel.rejectUser(user.uid)
                                    navController.previousBackStackEntry?.savedStateHandle?.set("needsRefresh", true)
                                    navController.popBackStack()
                                }
                            }) {
                                Text("Oui", color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRejectDialog = false }) {
                                Text("Annuler", color = colorResource(R.color.blue_medium))
                            }
                        },
                        containerColor = colorResource(R.color.white)
                    )
                }

                // Zoomed Image Dialog
                if (zoomedImageUrl != null) {
                    Dialog(
                        onDismissRequest = { zoomedImageUrl = null },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.9f))
                                .clickable { zoomedImageUrl = null },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .fillMaxHeight(0.8f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colorResource(R.color.white))
                                ) {
                                    AsyncImage(
                                        model = zoomedImageUrl,
                                        contentDescription = "Zoomed image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                IconButton(
                                    onClick = { zoomedImageUrl = null },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(colorResource(R.color.green_light))
                                ) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Fermer",
                                        tint = colorResource(R.color.white),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }

            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Aucun utilisateur trouvé.",
                        color = colorResource(R.color.blue_dark),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun DetailCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white))
    ) {
        Box(modifier = Modifier.padding(14.dp)) {
            content()
        }
    }
}

@Composable
fun ContactRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colorResource(R.color.green_teal),
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.blue_medium)
                )
            )
            Text(
                value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = colorResource(R.color.blue_dark)
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
fun DetailField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.blue_medium)
            )
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                color = colorResource(R.color.blue_dark)
            )
        )
    }
}

@Composable
fun DetailStatusBadge(status: Status) {
    val (bgColor, textColor) = when (status) {
        Status.VALIDATED -> colorResource(R.color.green_light) to colorResource(R.color.white)
        Status.PENDING -> colorResource(R.color.yellow_light) to colorResource(R.color.blue_dark)
        Status.REJECTED -> Color(0xFFEF5350) to colorResource(R.color.white)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Text(
            text = when (status) {
                Status.VALIDATED -> "Validé"
                Status.PENDING -> "En attente"
                Status.REJECTED -> "Rejeté"
            },
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
