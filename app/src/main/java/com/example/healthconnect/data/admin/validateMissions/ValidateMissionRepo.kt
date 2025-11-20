package com.example.healthconnect.data.admin.validateMissions

interface ValidateMissionRepo {
    suspend fun verifyMission(missionId: String): Boolean

    // Reject a user (sets status to REJECTED)
    suspend fun rejectMission(missionId: String): Boolean
}