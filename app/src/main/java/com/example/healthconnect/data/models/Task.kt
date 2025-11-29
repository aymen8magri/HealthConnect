package com.example.healthconnect.data.models

import androidx.compose.runtime.DontMemoize
import com.google.firebase.firestore.DocumentId

/**
 * Représente une tâche appartenant à une mission.
 */
data class Task(
    @DocumentId
    val idTask: String = "",
    val missionId: String = "",
    val roleType: Roles = Roles.VOLONTAIRE,
    val nbrMaxParticipants: Int = 0,
    val description: String = "",
    val participantIds: List<String> = emptyList(),
    val method: String? = null
)