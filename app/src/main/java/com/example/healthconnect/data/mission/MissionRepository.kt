package com.example.healthconnect.data.mission


interface MissionRepository {
    suspend fun getAllMissions(): List<Mission>
}
