package com.example.healthconnect.ui.missiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.admin.validateUsers.UserRepository
import com.example.healthconnect.data.models.Mission
import com.example.healthconnect.data.mission.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ... (imports)

@HiltViewModel
class MissionDetailViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _mission = MutableStateFlow<Mission?>(null)
    val mission: StateFlow<Mission?> = _mission
    private val _isCurrentUserCoordinator = MutableStateFlow(false)
    val isCurrentUserCoordinator: StateFlow<Boolean> = _isCurrentUserCoordinator.asStateFlow()

    fun loadMission(missionId: String) {
        viewModelScope.launch {
            // --- UTILISEZ LA NOUVELLE MÉTHODE EFFICACE ---
            _mission.value = missionRepository.getMissionById(missionId)
            _isCurrentUserCoordinator.value = userRepository.isCurrentUserCoordinatorVerified()
        }
    }
}