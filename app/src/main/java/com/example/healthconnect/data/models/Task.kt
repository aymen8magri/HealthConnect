package com.example.healthconnect.data.models

/**
 * Représente une tâche appartenant à une mission.
 */
data class Task(
    val idTask: String = "",
    val missionId: String = "",
    val roleType: Roles = Roles.VOLONTAIRE,
    val nbrMaxParticipants: Int = 0,
    val description: String = "",
    val participantIds: List<String> = emptyList(),
    val method: String? = null
)