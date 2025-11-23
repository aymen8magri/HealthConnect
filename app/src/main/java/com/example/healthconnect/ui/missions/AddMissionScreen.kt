package com.example.healthconnect.ui.missions

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.focus.onFocusChanged
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.healthconnect.R
import com.example.healthconnect.data.models.Roles
import com.example.healthconnect.data.models.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMissionScreen(
    navController: NavController,
    viewModel: AddMissionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val locationDisplay by viewModel.locationDisplay.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val photoUri by viewModel.photoUri.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val nbrMed by viewModel.nbrMedecins.collectAsState()
    val nbrVol by viewModel.nbrVolontaires.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saved by viewModel.saved.collectAsState()

    val blueDark = colorResource(R.color.blue_dark)
    val blueMedium = colorResource(R.color.blue_medium)
    val greenTeal = colorResource(R.color.green_teal)
    val greenLight = colorResource(R.color.green_light)
    val yellowLight = colorResource(R.color.yellow_light)

    // Image picker
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.onPhotoSelected(uri)
        viewModel.validateAll()
    }

    // Date pickers with validation
    val calendar = Calendar.getInstance()
    val today = startOfDay(System.currentTimeMillis())

    val datePickerStart = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth, 0, 0, 0)
                viewModel.onStartDateSelected(cal.timeInMillis)
                viewModel.validateAll()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = today
        }
    }

    val datePickerEnd = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth, 0, 0, 0)
                viewModel.onEndDateSelected(cal.timeInMillis)
                viewModel.validateAll()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Location picker dialog
    var showLocationDialog by remember { mutableStateOf(false) }
    var locCountry by remember { mutableStateOf("") }
    var locCity by remember { mutableStateOf("") }
    var locFullDetails by remember { mutableStateOf("") }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Sélectionner la localisation", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = locCountry,
                        onValueChange = { locCountry = it },
                        label = { Text("Pays") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = locCity,
                        onValueChange = { locCity = it },
                        label = { Text("Ville") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = locFullDetails,
                        onValueChange = { locFullDetails = it },
                        label = { Text("Détails (adresse, coords)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val display = "${locCountry}, ${locCity}".trim().trim(',')
                    val full = "${locCountry}, ${locCity} - ${locFullDetails}".trim().trim(',')
                    viewModel.onLocationSelected(display, full)
                    viewModel.validateAll()
                    showLocationDialog = false
                }) { Text("Valider", color = greenTeal, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Annuler", color = blueDark)
                }
            }
        )
    }

    // Observe saved
    LaunchedEffect(saved) {
        if (saved == true) {
            Toast.makeText(context, "Mission enregistrée avec succès", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else if (saved == false) {
            Toast.makeText(context, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show()
        }
    }

    // Helper to get error for a field
    fun getFieldErrors(fieldName: String): List<String> {
        return errors.filter { it.contains(fieldName, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Ajouter une mission", fontWeight = FontWeight.Bold, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = blueDark)
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- TITRE ---
            Text("Titre", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = blueDark)
            val titleErrors = getFieldErrors("titre")
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Ex: Campagne de vaccination") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .onFocusChanged { if (!it.isFocused) viewModel.validateAll() },
                shape = RoundedCornerShape(8.dp),
                isError = titleErrors.isNotEmpty(),
                singleLine = true
            )
            if (titleErrors.isNotEmpty()) {
                ErrorMessage(titleErrors.first())
            }

            // --- DESCRIPTION ---
            Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = blueDark)
            val descErrors = getFieldErrors("description")
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text("Détails sur la mission...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .onFocusChanged { if (!it.isFocused) viewModel.validateAll() },
                shape = RoundedCornerShape(8.dp),
                isError = descErrors.isNotEmpty()
            )
            if (descErrors.isNotEmpty()) {
                ErrorMessage(descErrors.first())
            }

            // --- DATES ---
            Text("Dates", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = blueDark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val startErrors = getFieldErrors("début")
                    OutlinedButton(
                        onClick = { datePickerStart.show() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (startErrors.isEmpty()) blueMedium else Color.Red
                        ),
                        border = BorderStroke(1.dp, if (startErrors.isEmpty()) blueMedium else Color.Red)
                    ) {
                        Icon(imageVector = Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (startDate == null) "Date début" else formatDate(startDate!!),
                            fontSize = 14.sp
                        )
                    }
                    if (startErrors.isNotEmpty()) {
                        ErrorMessage(startErrors.first(), fontSize = 12.sp)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    val endErrors = getFieldErrors("fin")
                    OutlinedButton(
                        onClick = {
                            // ensure end date cannot be before selected startDate (or today)
                            datePickerEnd.datePicker.minDate = startDate ?: today
                            datePickerEnd.show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (endErrors.isEmpty()) blueMedium else Color.Red
                        ),
                        border = BorderStroke(1.dp, if (endErrors.isEmpty()) blueMedium else Color.Red)
                    ) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (endDate == null) "Date fin" else formatDate(endDate!!),
                            fontSize = 14.sp
                        )
                    }
                    if (endErrors.isNotEmpty()) {
                        ErrorMessage(endErrors.first(), fontSize = 12.sp)
                    }
                }
            }

            // --- LOCALISATION ---
            Text("Localisation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = blueDark)
            val locErrors = getFieldErrors("localisation")
            OutlinedTextField(
                value = locationDisplay,
                onValueChange = {},
                label = { Text("Sélectionner une localisation") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLocationDialog = true }
                    .background(Color.White, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                readOnly = true,
                isError = locErrors.isNotEmpty(),
                trailingIcon = {
                    Icon(Icons.Default.Image, contentDescription = null, tint = blueMedium)
                }
            )
            if (locErrors.isNotEmpty()) {
                ErrorMessage(locErrors.first())
            }

            // --- PHOTO ---
            Text("Photo de la mission", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = blueDark)
            val photoErrors = getFieldErrors("image")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(yellowLight, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { launcher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = greenTeal),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "Choisir une image", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Choisir une image", color = Color.White)
                        }
                    }
                    if (!photoUri.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "photo mission",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            if (photoErrors.isNotEmpty()) {
                ErrorMessage(photoErrors.first())
            }

            // --- TÂCHES ---
            Text("Tâches", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = blueDark)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ajouter des tâches pour les participants", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.weight(1f))
                FloatingActionButton(
                    onClick = { viewModel.addEmptyTask(); viewModel.validateAll() },
                    containerColor = greenLight,
                    contentColor = Color.White,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter tâche")
                }
            }

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aucune tâche ajoutée", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                }
            }

            tasks.forEachIndexed { index, task ->
                TaskItem(
                    taskIndex = index + 1,
                    task = task,
                    onDelete = { viewModel.removeTask(task.idTask); viewModel.validateAll() },
                    onChange = { role, nbr, desc -> viewModel.updateTask(task.idTask, role, nbr, desc); viewModel.validateAll() },
                    blueMedium = blueMedium,
                    greenTeal = greenTeal
                )
            }

            // --- RÉCAPITULATIF ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Récapitulatif", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = blueDark)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        InfoBox("Médecins", nbrMed.toString(), greenTeal)
                        InfoBox("Volontaires", nbrVol.toString(), greenLight)
                    }
                }
            }

            // --- BOUTON ENREGISTRER ---
            Button(
                onClick = { viewModel.saveMission() },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenTeal)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Enregistrer la mission", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InfoBox(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier

            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun ErrorMessage(message: String, fontSize: TextUnit = 12.sp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = "Erreur",
            tint = Color.Red,
            modifier = Modifier.size(16.dp)
        )
        Text(message, color = Color.Red, fontSize = fontSize)
    }
}

@Composable
fun TaskItem(
    taskIndex: Int,
    task: Task,
    onDelete: () -> Unit,
    onChange: (role: Roles, nbr: Int, desc: String) -> Unit,
    blueMedium: Color,
    greenTeal: Color
) {
    var role by remember { mutableStateOf(task.roleType) }
    var nbr by remember { mutableStateOf(task.nbrMaxParticipants.toString()) }
    var desc by remember { mutableStateOf(task.description) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tâche $taskIndex", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = blueMedium)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer tâche", tint = Color.Red)
                }
            }

            Text("Rôle", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = blueMedium)
            SegmentedRoleSelector(
                current = role,
                onSelect = { role = it; onChange(it, nbr.toIntOrNull() ?: 0, desc) },
                greenTeal = greenTeal
            )

            Text("Nombre max de participants", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = blueMedium)
            /*OutlinedTextField(
                value = nbr,
                onValueChange = {
                    val filtered = it.filter { ch -> ch.isDigit() }
                    nbr = filtered
                    onChange(role, filtered.toIntOrNull() ?: 0, desc)
                },
                label = { Text("Ex: 5") },
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number,
                shape = RoundedCornerShape(8.dp)
            )*/

            Text("Description", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = blueMedium)
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it; onChange(role, nbr.toIntOrNull() ?: 0, desc) },
                label = { Text("Détails de la tâche...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun SegmentedRoleSelector(current: Roles, onSelect: (Roles) -> Unit, greenTeal: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = current == Roles.MEDECIN,
            onClick = { onSelect(Roles.MEDECIN) },
            label = { Text("Médecin") },
            modifier = Modifier.weight(1f),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = greenTeal,
                selectedLabelColor = Color.White
            )
        )
        FilterChip(
            selected = current == Roles.VOLONTAIRE,
            onClick = { onSelect(Roles.VOLONTAIRE) },
            label = { Text("Volontaire") },
            modifier = Modifier.weight(1f),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = greenTeal,
                selectedLabelColor = Color.White
            )
        )
    }
}

private fun formatDate(epochMillis: Long): String {
    if (epochMillis <= 0L) return "-"
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}

private fun startOfDay(epoch: Long): Long {
    val cal = Calendar.getInstance().apply { timeInMillis = epoch }
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}
