package com.example.healthconnect.data.participation

import com.example.healthconnect.data.models.Participation
import com.example.healthconnect.data.models.Status
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParticipationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ParticipationRepository {

    private val participationCollection = firestore.collection("participations")

    // ------------------------------
    // Get participation by mission ID
    // ------------------------------
    override suspend fun getParticipationByMissionId(missionId: String): List<Participation> {
        return participationCollection
            .whereEqualTo("missionId", missionId)
            .get()
            .await()
            .toObjects(Participation::class.java) // @DocumentId will inject document ID automatically
    }

    // ------------------------------
    // Get task list by user id
    // ------------------------------
    override suspend fun getTaskListByUserId(userId: String): List<String> {
        val participations = participationCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Participation::class.java)

        return participations.flatMap { it.assignedTaskIds }
    }

    // ------------------------------
    // Validate participation
    // ------------------------------
    override suspend fun validateParticipation(participationId: String) {
        participationCollection.document(participationId)
            .update("validation", Status.VALIDATED)
            .await()
    }

    // ------------------------------
    // Reject participation
    // ------------------------------
    override suspend fun rejectParticipation(participationId: String) {
        participationCollection.document(participationId)
            .update("validation", Status.REJECTED)
            .await()
    }

    // ------------------------------
    // Update participation
    // ------------------------------
    override suspend fun updateParticipation(participation: Participation) {
        val docRef = if (participation.id.isBlank()) participationCollection.document() else participationCollection.document(participation.id)
        docRef.set(participation).await()
    }

    // ------------------------------
    // Create participation
    // ------------------------------
    override suspend fun createParticipation(participation: Participation) {
        val docRef = if (participation.id.isBlank()) participationCollection.document() else participationCollection.document(participation.id)
        docRef.set(participation).await()
    }

    // ------------------------------
    // Delete participation
    // ------------------------------
    override suspend fun deleteParticipation(participationId: String) {
        participationCollection.document(participationId).delete().await()
    }
}
