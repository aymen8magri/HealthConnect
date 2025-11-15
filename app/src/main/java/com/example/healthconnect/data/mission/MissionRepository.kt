package com.example.healthconnect.data.mission


interface MissionRepository {
    suspend fun getAllMissions(): List<Mission>
    suspend fun getMissionById(missionId: String): Mission?
}
