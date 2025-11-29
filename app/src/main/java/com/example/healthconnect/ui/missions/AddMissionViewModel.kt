package com.example.healthconnect.ui.missions

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.models.Mission
import com.example.healthconnect.data.models.Task
import com.example.healthconnect.data.mission.MissionRepository
import com.example.healthconnect.data.models.Roles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class AddMissionViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    // MODIFIÉ: Garde une trace de la mission en cours d'édition et son ID
    private var currentMission: Mission? = null
    private var currentMissionId: String? = null

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _startDate = MutableStateFlow<Long?>(null)
    val startDate: StateFlow<Long?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Long?>(null)
    val endDate: StateFlow<Long?> = _endDate.asStateFlow()

    private val _photoUri = MutableStateFlow<String?>(null)
    val photoUri: StateFlow<String?> = _photoUri.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _nbrMedecins = MutableStateFlow(0)
    val nbrMedecins: StateFlow<Int> = _nbrMedecins.asStateFlow()

    private val _nbrVolontaires = MutableStateFlow(0)
    val nbrVolontaires: StateFlow<Int> = _nbrVolontaires.asStateFlow()

    private val _errors = MutableStateFlow<List<String>>(emptyList())
    val errors: StateFlow<List<String>> = _errors.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saved = MutableStateFlow<Boolean?>(null)
    val saved: StateFlow<Boolean?> = _saved.asStateFlow()

    fun onTitleChange(v: String) { _title.value = v }
    fun onDescriptionChange(v: String) { _description.value = v }
    fun onLocationChange(v: String) { _location.value = v }

    fun onStartDateSelected(epochMillis: Long) { _startDate.value = epochMillis }
    fun onEndDateSelected(epochMillis: Long) { _endDate.value = epochMillis }

    fun onPhotoSelected(uri: Uri?) { _photoUri.value = uri?.toString() }

    fun addEmptyTask() {
        val new = Task(
            idTask = UUID.randomUUID().toString(),
            missionId = "",
            roleType = Roles.VOLONTAIRE,
            nbrMaxParticipants = 1,
            description = "",
            participantIds = emptyList()
        )
        _tasks.value = _tasks.value + new
        recomputeCounts()
    }

    fun removeTask(taskId: String) {
        _tasks.value = _tasks.value.filter { it.idTask != taskId }
        recomputeCounts()
    }

    fun updateTask(taskId: String, role: Roles? = null, nbrMax: Int? = null, description: String? = null) {
        _tasks.value = _tasks.value.map { t ->
            if (t.idTask != taskId) t else t.copy(
                roleType = role ?: t.roleType,
                nbrMaxParticipants = nbrMax ?: t.nbrMaxParticipants,
                description = description ?: t.description
            )
        }
        recomputeCounts()
    }
// ... (au début de la classe AddMissionViewModel)

    // NOUVELLE FONCTION : Pour nettoyer le formulaire avant un nouvel ajout
    fun resetState() {
        currentMission = null
        currentMissionId = null
        _title.value = ""
        _description.value = ""
        _location.value = ""
        _startDate.value = null
        _endDate.value = null
        _photoUri.value = null
        _tasks.value = emptyList()
        _errors.value = emptyList()
        _saved.value = null // Très important pour que le Toast ne se déclenche pas à nouveau
        recomputeCounts()
    }

// ... (le reste du ViewModel reste inchangé)

    private fun recomputeCounts() {
        val med = _tasks.value.filter { it.roleType == Roles.MEDECIN }.sumOf { it.nbrMaxParticipants }
        val vol = _tasks.value.filter { it.roleType == Roles.VOLONTAIRE }.sumOf { it.nbrMaxParticipants }
        _nbrMedecins.value = med
        _nbrVolontaires.value = vol
    }

    fun validateAll(): Boolean {
        val errs = mutableListOf<String>()
        if (title.value.isBlank()) errs.add("Le titre est requis")
        if (description.value.isBlank()) errs.add("La description est requise")
        if (location.value.isBlank()) errs.add("La localisation est requise")
        val s = startDate.value
        val e = endDate.value
        val today = System.currentTimeMillis()
        if (s == null) errs.add("La date de début est requise")
        // En mode édition, la date de début peut être dans le passé
        else if (currentMissionId == null && s < startOfDay(today)) errs.add("La date de début ne peut pas précéder aujourd'hui")
        if (e == null) errs.add("La date de fin est requise")
        else if (s != null && e < s) errs.add("La date de fin ne peut pas précéder la date de début")
        if (tasks.value.isEmpty()) errs.add("Au moins une tâche est requise")
        tasks.value.forEachIndexed { idx, t ->
            if (t.description.isBlank()) errs.add("La description est requise pour la tâche ${idx + 1}")
            if (t.nbrMaxParticipants <= 0) errs.add("Le nombre max de participants doit être > 0 pour la tâche ${idx + 1}")
        }
        if (photoUri.value.isNullOrBlank()) errs.add("L'image de la mission est requise")

        _errors.value = errs
        return errs.isEmpty()
    }

    private fun startOfDay(epoch: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = epoch }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // NOUVEAU: Fonction pour charger les données de la mission à éditer
    fun loadMissionForEdit(missionId: String) {
        if (missionId == currentMissionId) return // Déjà chargé
        currentMissionId = missionId
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val mission = missionRepository.getMissionById(missionId)
                if (mission != null) {
                    currentMission = mission // Stocker la mission originale
                    _title.value = mission.title ?: ""
                    _description.value = mission.description ?: ""
                    _location.value = mission.location ?: ""
                    _startDate.value = mission.startDate?.toDate()?.time
                    _endDate.value = mission.endDate?.toDate()?.time
                    _photoUri.value = mission.photoMission
                    // NOTE: La logique pour charger les tâches associées (listTasks) doit être implémentée
                    @Suppress("UNCHECKED_CAST")
                    _tasks.value = (mission.listTasks as? List<Task>) ?: emptyList()
                    recomputeCounts()
                    recomputeCounts()
                } else {
                    _errors.value = listOf("Impossible de trouver la mission à modifier.")
                }
            } catch (e: Exception) {
                _errors.value = listOf("Erreur de chargement: ${e.message}")
            } finally {
                _isSaving.value = false
            }
        }
    }

    // MODIFIÉ: Renommée et transformée en dispatcher
// Dans AddMissionViewModel.kt

    fun saveOrUpdateMission(): Boolean {
        // La validation en premier
        if (!validateAll()) {
            return false  // ❗ On retourne false si la validation échoue
        }

        // Si la validation est OK → sauvegarde
        viewModelScope.launch {
            if (currentMissionId == null) {
                addMission()
            } else {
                updateMission()
            }
        }

        return true  // ❗ Validation réussie
    }



    private suspend fun addMission() {
        _isSaving.value = true
        val startTimestamp = startDate.value?.let { Timestamp(Date(it)) }
        val endTimestamp = endDate.value?.let { Timestamp(Date(it)) }

        val coordinatorId = auth.currentUser?.uid
        if (coordinatorId == null) {
            _errors.value = _errors.value + "L'utilisateur n'est pas authentifié."
            _isSaving.value = false
            return
        }

        // Crée un nouvel ID unique pour la mission
        val newMissionId = UUID.randomUUID().toString()

        val mission = Mission(
            id = newMissionId, // Utilise le nouvel ID
            title = title.value,
            description = description.value,
            location = location.value,
            startDate = startTimestamp,
            endDate = endTimestamp,
            status = com.example.healthconnect.data.models.MissionStatus.PLANNED,
            validationStatus = com.example.healthconnect.data.models.Status.PENDING,
            nbrMedecins = nbrMedecins.value,
            nbrVolontaires = nbrVolontaires.value,
            taskIds = _tasks.value.map { it.idTask },
            participantIds = emptyList(),
            photoMission = photoUri.value ?: "",
            listTasks = _tasks.value.map { it.copy(missionId = newMissionId) }, // Assigne l'ID de la mission aux tâches
            coordinatorId = coordinatorId
        )
        val res = missionRepository.addMission(mission)
        _isSaving.value = false
        _saved.value = res
    }


    private suspend fun updateMission() {
        val missionId = currentMissionId ?: return
        val originalMission = currentMission ?: return

        _isSaving.value = true
        val startTimestamp = startDate.value?.let { Timestamp(Date(it)) }
        val endTimestamp = endDate.value?.let { Timestamp(Date(it)) }

        val updatedMission = originalMission.copy(
            title = title.value,
            description = description.value,
            location = location.value,
            startDate = startTimestamp,
            endDate = endTimestamp,
            photoMission = photoUri.value ?: "",
            listTasks = _tasks.value.map { it.copy(missionId = missionId) },
            // CORRECTION : Supprimez le type explicite <String> qui est mal placé.
            // Le compilateur peut maintenant déduire correctement que le résultat est une List<String>.
            taskIds = _tasks.value.map { it.idTask },
            nbrMedecins = nbrMedecins.value,
            nbrVolontaires = nbrVolontaires.value
        )

        val res = missionRepository.updateMission(updatedMission)

        _isSaving.value = false
        _saved.value = res
    }


}
