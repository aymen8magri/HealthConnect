package com.example.healthconnect.data.admin.validateMissions

import javax.inject.Inject

class ValidateMissionImpl @Inject constructor() : ValidateMissionRepo {
    override suspend fun verifyMission(missionId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun rejectMission(missionId: String): Boolean {
        TODO("Not yet implemented")
    }

}