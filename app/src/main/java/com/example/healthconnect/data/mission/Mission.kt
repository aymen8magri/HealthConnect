package com.example.healthconnect.data.mission

import com.google.firebase.firestore.DocumentId

/**
 * Représente une mission dans l'application.
 * Ce modèle de données est conçu pour être compatible avec Firestore.
 *
 * @property id L'identifiant unique de la mission, automatiquement mappé par Firestore grâce à @DocumentId.
 * @property title Le titre de la mission.
 * @property description Une description détaillée de la mission.
 * @property coordinatorId L'ID de l'utilisateur (le coordinateur) qui a créé la mission.
 * @property volunteers L'ID des volontaires qui ont rejoint la mission.
 * @property doctors L'ID des docteurs qui ont rejoint la mission.
 * ... autres champs ...
 */
data class Mission(
    @DocumentId val id: String, // @DocumentId pour un mappage automatique avec l'ID du document Firestore
    val title: String,
    val description: String,
    val location: String,
    val date: String = "",
    val startTime: String,
    val endTime: String,
    val status: MissionStatus = MissionStatus.PLANNED,

    // --- Champs de participants mis à jour ---
    val coordinatorId: String, // ID de l'utilisateur qui a créé la mission
    val volunteers: List<String> = emptyList(), // Liste des IDs des volontaires inscrits
    val doctors: List<String> = emptyList(), // Liste des IDs des docteurs inscrits

    val maxVolunteers: Int,
    val maxDoctors: Int,
    val materialRequired: List<String> = emptyList(),
    val imageUrl: String? = null
)

enum class MissionStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
