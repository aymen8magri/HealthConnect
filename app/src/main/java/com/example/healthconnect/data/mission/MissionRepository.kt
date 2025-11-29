package com.example.healthconnect.data.mission

import com.example.healthconnect.data.models.Mission


interface MissionRepository {
    suspend fun getAllMissions(): List<Mission>
    suspend fun getMissionById(missionId: String): Mission?
    suspend fun addMission(mission: Mission): Boolean
    suspend fun updateMission(mission: Mission): Boolean
    suspend fun deleteMission(missionId: String): Boolean
    suspend fun getMissionsByCoordinatorId(coordinatorId: String): List<Mission>

    suspend fun verifyMission(missionId: String): Unit

    // Reject a user (sets status to REJECTED)
    suspend fun rejectMission(missionId: String): Unit
}
