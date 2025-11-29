package com.example.healthconnect.data.mission

import com.example.healthconnect.data.models.Mission
import com.example.healthconnect.data.models.Status
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MissionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MissionRepository {

    private val missionCollection = firestore.collection("missions")

    override suspend fun getAllMissions(): List<Mission> {
        return try {
            missionCollection.get().await().toObjects(Mission::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMissionById(missionId: String): Mission? {
        return try {
            missionCollection.document(missionId).get().await().toObject(Mission::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addMission(mission: Mission): Boolean {
        return try {
            val docRef = if (mission.id.isBlank()) missionCollection.document() else missionCollection.document(mission.id)
            val toSave = mission.copy(id = docRef.id)
            docRef.set(toSave).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateMission(mission: Mission): Boolean {
        return try {
            missionCollection.document(mission.id).set(mission).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteMission(missionId: String): Boolean {
        return try {
            missionCollection.document(missionId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun verifyMission(missionId: String) {
        val mission = getMissionById(missionId)
        if (mission != null) {
            mission.validationStatus = Status.VALIDATED
            updateMission(mission)
        }
    }

    override suspend fun rejectMission(missionId: String) {
        val mission = getMissionById(missionId)
        if (mission != null) {
            mission.validationStatus = Status.REJECTED
            updateMission(mission)
        }
    }
}
