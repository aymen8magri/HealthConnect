package com.example.healthconnect.ui.missiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.mission.Mission
import com.example.healthconnect.data.mission.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ... (imports)

@HiltViewModel
class MissionDetailViewModel @Inject constructor(
    private val missionRepository: MissionRepository
) : ViewModel() {

    private val _mission = MutableStateFlow<Mission?>(null)
    val mission: StateFlow<Mission?> = _mission

    fun loadMission(missionId: String) {
        viewModelScope.launch {
            // --- UTILISEZ LA NOUVELLE MÉTHODE EFFICACE ---
            _mission.value = missionRepository.getMissionById(missionId)
        }
    }
}

