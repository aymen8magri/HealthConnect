package com.example.healthconnect.data.mission

import com.example.healthconnect.data.models.Mission


interface MissionRepository {
    suspend fun getAllMissions(): List<Mission>
    suspend fun getMissionById(missionId: String): Mission?
    suspend fun addMission(mission: Mission): Boolean
}
