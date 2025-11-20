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

@HiltViewModel
class AddMissionViewModel @Inject constructor(
    private val missionRepository: MissionRepository
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _locationDisplay = MutableStateFlow("")
    val locationDisplay: StateFlow<String> = _locationDisplay.asStateFlow()

    private val _locationFull = MutableStateFlow("")
    val locationFull: StateFlow<String> = _locationFull.asStateFlow()

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
    fun onLocationSelected(display: String, full: String) {
        _locationDisplay.value = display
        _locationFull.value = full
    }

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
        if (locationFull.value.isBlank()) errs.add("La localisation est requise")
        val s = startDate.value
        val e = endDate.value
        val today = System.currentTimeMillis()
        if (s == null) errs.add("La date de début est requise")
        else if (s < startOfDay(today)) errs.add("La date de début ne peut pas précéder aujourd'hui")
        if (e == null) errs.add("La date de fin est requise")
        else if (s != null && e < s) errs.add("La date de fin ne peut pas précéder la date de début")
        if (tasks.value.isEmpty()) errs.add("Au moins une tâche est requise")
        // ensure each task fields
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

    fun canSave(): Boolean {
        // permitted if no validation errors
        return validateAll()
    }

    fun saveMission() {
        viewModelScope.launch {
            if (!validateAll()) return@launch
            _isSaving.value = true
            val mission = Mission(
                idMission = "",
                title = title.value,
                description = description.value,
                location = locationFull.value,
                startDate = startDate.value ?: 0L,
                endDate = endDate.value ?: 0L,
                status = com.example.healthconnect.data.models.MissionStatus.PLANNED,
                validationStatus = com.example.healthconnect.data.models.Status.PENDING,
                nbrMedecins = nbrMedecins.value,
                nbrVolontaires = nbrVolontaires.value,
                taskIds = _tasks.value.map { it.idTask },
                participantIds = emptyList(),
                photoMission = photoUri.value ?: "",
                listTasks = _tasks.value
            )
            val res = missionRepository.addMission(mission)
            _isSaving.value = false
            _saved.value = res
        }
    }
}

