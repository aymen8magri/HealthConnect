package com.example.healthconnect.ui.profile

 import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.healthconnect.R
import com.example.healthconnect.data.models.Roles
import com.example.healthconnect.data.models.User
import com.example.healthconnect.navigation.AppRoutes

@Composable
fun ProfileViewContent(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val editedUser by viewModel.editedUser.collectAsState()

    if (isEditing && editedUser != null) {
        EditProfileScreen(
            user = editedUser!!,
            onCancel = { viewModel.cancelEditing() },
            onSave = { updatedUser ->
                viewModel.updateUserField { updatedUser }
                viewModel.saveChanges()
            },
            onFieldChange = { block -> viewModel.updateUserField(block) }
        )
    } else if (currentUser != null) {
        ViewProfileScreen(
            user = currentUser!!,
            navController = navController,
            onLogout = onLogout,
            onEditClick = { viewModel.startEditing() }
        )
    } else {
        // Loading state with a debug button to force a test user (user_1)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.white)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = colorResource(R.color.blue_dark)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Debug button - only for local testing
                Button(
                    onClick = { viewModel.forceSetCurrentUserForTest("user_1") },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.green_light)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Forcer user_1 (debug)")
                }
            }
        }
    }
}

@Composable
fun ViewProfileScreen(
    user: User,
    navController: NavController,
    onLogout: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header avec image de profil
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.green_light))
        ) {
            AsyncImage(
                model = user.image,
                contentDescription = "Profile image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nom et email
        Text(
            text = user.fullName,
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineSmall,
            color = colorResource(R.color.blue_dark)
        )

        Text(
            text = user.email,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.blue_medium)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton d'édition
        Button(
            onClick = onEditClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.blue_medium)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp)
            )
            Text("Modifier le profil")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Carte d'informations générales
        ProfileInfoCard(title = "Informations générales") {
            ProfileInfoRow(label = "Âge", value = "${user.age} ans")
            Divider(color = colorResource(R.color.yellow_light), thickness = 1.dp)
            ProfileInfoRow(label = "Téléphone", value = user.phoneNumber ?: "N/A")
            Divider(color = colorResource(R.color.yellow_light), thickness = 1.dp)
            ProfileInfoRow(label = "Adresse", value = user.adresse)
            Divider(color = colorResource(R.color.yellow_light), thickness = 1.dp)
            ProfileInfoRow(label = "Bio", value = user.bio)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cartes spécifiques selon le rôle
        when (user.role) {
            Roles.MEDECIN -> {
                ProfileInfoCard(title = "Informations Médecin") {
                    ProfileInfoRow(label = "Cabinet", value = user.cabinet)
                    Divider(color = colorResource(R.color.yellow_light), thickness = 1.dp)
                    ProfileInfoRow(label = "Spécialité", value = user.specialiteMedecin.toString())
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (user.carteService.isNotEmpty()) {
                    Text(
                        text = "Carte Service",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorResource(R.color.blue_dark),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorResource(R.color.green_light))
                    ) {
                        AsyncImage(
                            model = user.carteService,
                            contentDescription = "Service card",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Roles.COORDINATEUR -> {
                ProfileInfoCard(title = "Informations Coordinateur") {
                    ProfileInfoRow(label = "Association", value = user.association)
                    Divider(color = colorResource(R.color.yellow_light), thickness = 1.dp)
                    ProfileInfoRow(label = "Poste", value = user.posteAsso)
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (user.carteAssociation.isNotEmpty()) {
                    Text(
                        text = "Carte Association",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorResource(R.color.blue_dark),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorResource(R.color.green_light))
                    ) {
                        AsyncImage(
                            model = user.carteAssociation,
                            contentDescription = "Association card",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            else -> {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Boutons spécifiques selon le rôle
        when (user.role) {
            Roles.COORDINATEUR -> {
                Button(
                    onClick = { navController.navigate("missions") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.green_teal)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "My missions",
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp)
                    )
                    Text("Mes missions")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { navController.navigate(AppRoutes.ADD_MISSION) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.green_light)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add mission",
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp)
                    )
                    Text("Ajouter une mission")
                }
            }

            else -> {
                Button(
                    onClick = { navController.navigate("missions") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.green_teal)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "My missions",
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp)
                    )
                    Text("Mes missions")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { navController.navigate("taches") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.green_light)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = "My tasks",
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp)
                    )
                    Text("Mes tâches")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bouton de déconnexion
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE74C3C)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp)
            )
            Text("Se déconnecter")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProfileInfoCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.yellow_light)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                color = colorResource(R.color.blue_dark)
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.blue_medium),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.blue_dark),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EditProfileScreen(
    user: User,
    onCancel: () -> Unit,
    onSave: (User) -> Unit,
    onFieldChange: ((User) -> User) -> Unit
) {
    var fullName by remember { mutableStateOf(user.fullName) }
    var phoneNumber by remember { mutableStateOf(user.phoneNumber ?: "") }
    var adresse by remember { mutableStateOf(user.adresse) }
    var bio by remember { mutableStateOf(user.bio) }
    var age by remember { mutableStateOf(user.age.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Modifier le profil",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineSmall,
            color = colorResource(R.color.blue_dark)
        )

        Spacer(modifier = Modifier.height(24.dp))

        EditableProfileField(
            label = "Nom complet",
            value = fullName,
            onValueChange = { fullName = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditableProfileField(
            label = "Email",
            value = user.email,
            onValueChange = {},
            enabled = false
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditableProfileField(
            label = "Âge",
            value = age,
            onValueChange = { age = it },
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditableProfileField(
            label = "Téléphone",
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditableProfileField(
            label = "Adresse",
            value = adresse,
            onValueChange = { adresse = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditableProfileField(
            label = "Bio",
            value = bio,
            onValueChange = { bio = it },
            singleLine = false,
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.blue_medium)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Annuler")
            }

            Button(
                onClick = {
                    val updatedUser = user.copy(
                        fullName = fullName,
                        phoneNumber = phoneNumber,
                        adresse = adresse,
                        bio = bio,
                        age = age.toIntOrNull() ?: user.age
                    )
                    onSave(updatedUser)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.green_light)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Enregistrer")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun EditableProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.blue_dark),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(R.color.yellow_light),
                    shape = RoundedCornerShape(8.dp)
                ),
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(R.color.yellow_light),
                unfocusedContainerColor = colorResource(R.color.yellow_light),
                focusedIndicatorColor = colorResource(R.color.green_light),
                unfocusedIndicatorColor = colorResource(R.color.blue_medium)
            )
        )
    }
}
