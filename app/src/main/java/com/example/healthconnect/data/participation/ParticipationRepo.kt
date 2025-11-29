package com.example.healthconnect.data.participation

import com.example.healthconnect.data.models.Participation
import com.example.healthconnect.data.models.Status

interface ParticipationRepository {

    suspend fun getParticipationByMissionId(missionId: String): List<Participation>

    suspend fun getTaskListByUserId(userId: String): List<String>

    suspend fun validateParticipation(participationId: String)

    suspend fun rejectParticipation(participationId: String)

    suspend fun updateParticipation(participation: Participation)

    suspend fun createParticipation(participation: Participation)

    suspend fun deleteParticipation(participationId: String)

    suspend fun getParticipationsForUserInMission(userId: String, missionId: String): List<Participation>
}
