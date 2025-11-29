package com.example.healthconnect.ui.missiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.admin.validateUsers.UserRepository
import com.example.healthconnect.data.mission.MissionRepository
import com.example.healthconnect.data.models.*
import com.example.healthconnect.data.participation.ParticipationRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionDetailViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val participationRepository: ParticipationRepository,
    private val userRepository: UserRepository, // AJOUT : Pour lire le profil utilisateur
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _mission = MutableStateFlow<Mission?>(null)
    val mission: StateFlow<Mission?> = _mission

    private val _userParticipations = MutableStateFlow<List<Participation>>(emptyList())
    val userParticipations: StateFlow<List<Participation>> = _userParticipations.asStateFlow()

    // MODIFICATION : Utilise votre enum `Roles` et est nullable pour les invités.
    private val _activeUserRole = MutableStateFlow<Roles?>(null)
    val activeUserRole: StateFlow<Roles?> = _activeUserRole.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    fun loadMission(missionId: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _activeUserRole.value = null // L'utilisateur est un invité (non connecté)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Étape 1 : Charger la mission
                val missionResult = missionRepository.getMissionById(missionId)
                _mission.value = missionResult

                // Étape 2 : Charger le profil de l'utilisateur pour connaître son rôle
                val userProfile = userRepository.getUserById(userId) // <-- CORRECTION : Utilise la fonction existante
                // Étape 3 : Déterminer le rôle actif
                determineActiveRole(missionResult, userProfile)

                // Étape 4 : Charger les participations de l'utilisateur pour cette mission
                val participationsResult = participationRepository.getParticipationsForUserInMission(userId, missionId)
                _userParticipations.value = participationsResult

            } catch (e: Exception) {
                _error.value = "Erreur de chargement: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun determineActiveRole(mission: Mission?, user: User?) {
        val currentUserId = auth.currentUser?.uid
        val userRoleFromProfile = user?.role

        if (mission == null || currentUserId == null || userRoleFromProfile == null) {
            _activeUserRole.value = null // L'utilisateur est un invité ou une erreur s'est produite
            return
        }

        // Logique de rôle précise :
        // 1. Si l'ID de l'utilisateur correspond à celui du coordinateur de la mission, son rôle est COORDINATEUR.
        if (mission.coordinatorId == currentUserId) {
            _activeUserRole.value = Roles.COORDINATEUR
        }
        // 2. Sinon, on utilise le rôle de son profil (VOLONTAIRE, MEDECIN, ADMIN).
        else {
            _activeUserRole.value = userRoleFromProfile
        }
    }

    fun applyForTask(missionId: String, taskId: String) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newParticipation = Participation(
                    missionId = missionId,
                    userId = userId,
                    validation = Status.PENDING,
                    assignedTaskIds = listOf(taskId)
                )
                participationRepository.createParticipation(newParticipation)
                loadMission(missionId) // Recharger pour voir la candidature
            } catch (e: Exception) {
                _error.value = "Erreur lors de la postulation: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMission(missionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                missionRepository.deleteMission(missionId)
                _isDeleted.value = true
            } catch (e: Exception) {
                _error.value = "Erreur lors de la suppression: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
