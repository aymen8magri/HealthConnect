package com.example.healthconnect.ui.admin.userList

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.healthconnect.R
import com.example.healthconnect.data.models.Roles
import com.example.healthconnect.data.models.Status
import com.example.healthconnect.data.models.User
import com.example.healthconnect.ui.admin.userList.UserListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListView(

    navController: NavHostController,
    missionId: String,
    viewModel: UserListViewModel = hiltViewModel(),
    onUserClick: (String) -> Unit
) {
    val users by viewModel.filteredUsers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val roleFilter by viewModel.roleFilter.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    LaunchedEffect(missionId) {
        viewModel.loadData(missionId)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Utilisateurs",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colorResource(R.color.white)
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rafraîchir", tint = colorResource(R.color.white))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.blue_medium)
                )
            )
        },
        containerColor = colorResource(R.color.yellow_light)
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Compact search and filter bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.blue_medium))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    placeholder = { Text("Chercher...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = colorResource(R.color.blue_dark),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorResource(R.color.white),
                        unfocusedContainerColor = colorResource(R.color.white),
                        focusedIndicatorColor = colorResource(R.color.green_teal),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black)
                    )
                )

                // Role filter chips (horizontal)
                Text(
                    "Rôle",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.white)
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
                if(viewModel.isAdmin){
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            CompactFilterChip(
                                selected = roleFilter == null,
                                onClick = { viewModel.onRoleFilterChange(null) },
                                label = "Tous"
                            )
                        }
                        items(Roles.entries.size) { idx ->
                            CompactFilterChip(
                                selected = roleFilter == Roles.entries[idx],
                                onClick = { viewModel.onRoleFilterChange(Roles.entries[idx]) },
                                label = when (Roles.entries[idx]) {
                                    Roles.MEDECIN -> "Médecin"
                                    Roles.COORDINATEUR -> "Coord."
                                    Roles.VOLONTAIRE -> "Volontaire"
                                    Roles.ADMIN -> "Admin"
                                }
                            )
                        }
                    }

                }
                if(viewModel.isCoordinator){
                // Choose which roles to show for this UI (admin vs coordinator logic should decide this list)
                    val allowedRoles = listOf(Roles.MEDECIN, Roles.VOLONTAIRE)

               // Map role -> label (no 'when' on the enum, so no exhaustiveness error)
                    val roleLabels = mapOf(
                        Roles.MEDECIN to "Médecin",
                        Roles.VOLONTAIRE to "Volontaire",
                        Roles.COORDINATEUR to "Coord.",
                        Roles.ADMIN to "Admin"
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            CompactFilterChip(
                                selected = roleFilter == null,
                                onClick = { viewModel.onRoleFilterChange(null) },
                                label = "Tous"
                            )
                        }

                        items(allowedRoles) { role ->
                            CompactFilterChip(
                                selected = roleFilter == role,
                                onClick = { viewModel.onRoleFilterChange(role) },
                                label = roleLabels[role] ?: role.name
                            )
                        }
                    }

                }


                // Status filter chips (horizontal)
                Text(
                    "Statut",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.white)
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        CompactFilterChip(
                            selected = statusFilter == null,
                            onClick = { viewModel.onStatusFilterChange(null) },
                            label = "Tous"
                        )
                    }
                    items(Status.entries.size) { idx ->
                        CompactFilterChip(
                            selected = statusFilter == Status.entries[idx],
                            onClick = { viewModel.onStatusFilterChange(Status.entries[idx]) },
                            label = when (Status.entries[idx]) {
                                Status.VALIDATED -> "Validé"
                                Status.PENDING -> "En attente"
                                Status.REJECTED -> "Rejeté"
                            }
                        )
                    }
                }
            }

            if (users.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Aucun utilisateur trouvé.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorResource(R.color.blue_dark).copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(users) { user ->
                        UserListCard(user = user, onClick = { onUserClick(user.uid) })
                    }
                }
            }
        }
    }
}

@Composable
fun CompactFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    val bgColor = if (selected) colorResource(R.color.green_light) else colorResource(R.color.white)
    val textColor = if (selected) colorResource(R.color.white) else colorResource(R.color.blue_dark)

    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .height(28.dp),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = if (!selected) {
            BorderStroke(1.dp, colorResource(R.color.blue_medium))
        } else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = textColor,
                maxLines = 1
            )
        }
    }
}

@Composable
fun UserListCard(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
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
                        fontSize = 16.sp
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    user.fullName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.blue_dark),
                    maxLines = 1
                )
                Text(
                    user.role.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = colorResource(R.color.blue_medium),
                    fontSize = 10.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CompactStatusBadge(status = user.statusRole)
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Détails",
                tint = colorResource(R.color.blue_medium),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun CompactStatusBadge(status: Status) {
    val (bgColor, textColor) = when (status) {
        Status.VALIDATED -> colorResource(R.color.green_light) to colorResource(R.color.white)
        Status.PENDING -> colorResource(R.color.yellow_light) to colorResource(R.color.blue_dark)
        Status.REJECTED -> Color(0xFFEF5350) to colorResource(R.color.white)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Text(
            text = when (status) {
                Status.VALIDATED -> "Validé"
                Status.PENDING -> "En attente"
                Status.REJECTED -> "Rejeté"
            },
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
        )
    }
}
