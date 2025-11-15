package com.example.healthconnect.ui.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.mission.Mission
import com.example.healthconnect.data.mission.MissionRepository
import com.example.healthconnect.data.mission.MissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val missionRepository: MissionRepository
) : ViewModel() {

    private val _allMissions = MutableStateFlow<List<Mission>>(emptyList())
    // Exposé publiquement en tant que StateFlow immuable
    val allMissions: StateFlow<List<Mission>> = _allMissions.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- CORRECTION : Une seule source de vérité pour le filtre de statut ---
    private val _statusFilter = MutableStateFlow<MissionStatus?>(null) // null = pas de filtre
    val statusFilter: StateFlow<MissionStatus?> = _statusFilter.asStateFlow()

    // --- CORRECTION : Le combine utilise maintenant les StateFlow publics ---
    val filteredMissions: StateFlow<List<Mission>> =
        combine(searchQuery, statusFilter, allMissions) { query, status, missions ->
            val filteredByQuery = if (query.isBlank()) {
                missions
            } else {
                missions.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }

            // Appliquer ensuite le filtre de statut
            if (status == null) {
                filteredByQuery // Retourne la liste filtrée par recherche si aucun statut n'est sélectionné
            } else {
                filteredByQuery.filter { it.status == status }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadMissions()
    }

    private fun loadMissions() {
        viewModelScope.launch {
            _allMissions.value = missionRepository.getAllMissions()
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // --- CORRECTION : La fonction met à jour _statusFilter ---
    fun onStatusFilterChange(newStatus: MissionStatus?) {
        _statusFilter.value = newStatus
    }

    // Les déclarations redondantes ont été supprimées.
}
