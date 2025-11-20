package com.example.healthconnect.data.mission

import com.example.healthconnect.data.models.Mission
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MissionRepositoryImpl @Inject constructor() : MissionRepository {

    private val missions = mutableListOf<Mission>()
    private val mutex = Mutex()

    override suspend fun getAllMissions(): List<Mission> {
        return mutex.withLock { missions.toList() }
    }

    override suspend fun getMissionById(missionId: String): Mission? {
        return mutex.withLock { missions.find { it.idMission == missionId } }
    }

    override suspend fun addMission(mission: Mission): Boolean {
        return mutex.withLock {
            val id = if (mission.idMission.isBlank()) UUID.randomUUID().toString() else mission.idMission
            val toAdd = mission.copy(idMission = id)
            missions.add(toAdd)
            true
        }
    }
}

