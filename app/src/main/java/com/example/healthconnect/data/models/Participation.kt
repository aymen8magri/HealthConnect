package com.example.healthconnect.data.models

/**
 * Représente la participation d'un utilisateur à une mission (table de jointure many-to-many).
 * - validation : statut de validation de la participation
 * - assignedTaskIds : tâches assignées à ce participant pour cette mission
 */
data class Participation(
    val id: String = "",
    val missionId: String = "",
    val userId: String = "",
    val validation: Status = Status.PENDING,
    val assignedTaskIds: List<String> = emptyList()
)