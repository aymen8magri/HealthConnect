package com.example.healthconnect.ui.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.models.Mission
import com.example.healthconnect.data.mission.MissionRepository
import com.example.healthconnect.data.models.MissionStatus
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MissionViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _allMissions = MutableStateFlow<List<Mission>>(emptyList())
    val allMissions: StateFlow<List<Mission>> = _allMissions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow<MissionStatus?>(null)
    val statusFilter: StateFlow<MissionStatus?> = _statusFilter.asStateFlow()

    val filteredMissions: StateFlow<List<Mission>> = combine(
        searchQuery,
        statusFilter,
        allMissions
    ) { query, status, missions ->
        var filtered = missions

        if (query.isNotBlank()) {
            filtered = filtered.filter {
                (it.title ?: "").contains(query, ignoreCase = true) ||
                        (it.description ?: "").contains(query, ignoreCase = true)
            }
        }


        if (status != null) {
            filtered = filtered.filter { it.status == status }
        }

        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )



    // 🔥 Nouvelle version Firebase-friendly
    fun loadMissions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val missions = missionRepository.getAllMissions()

                println("missions: $missions")


                _allMissions.value = missions
            } catch (e: Exception) {
                _errorMessage.value = "Erreur de chargement : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun loadMissionsForCurrentUser() {
        viewModelScope.launch {
            // Récupérer l'ID de l'utilisateur connecté
            val currentUserId = auth.currentUser?.uid

            if (currentUserId != null) {
                // Appeler la fonction du repository qui filtre par ID
                val userMissions = missionRepository.getMissionsByCoordinatorId(currentUserId)
                _allMissions.value = userMissions
            } else {
                // Gérer le cas où l'utilisateur n'est pas connecté
                // Vous pouvez émettre un état d'erreur ici
            }
        }
    }
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onStatusFilterChange(newStatus: MissionStatus?) {
        _statusFilter.value = newStatus
    }
}
