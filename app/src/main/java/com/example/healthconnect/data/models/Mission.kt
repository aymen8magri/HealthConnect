package com.example.healthconnect.data.models

/**
 * Représente une mission.
 * - relation one-to-many: mission -> tasks (taskIds)
 * - relation many-to-many with users via Participation (participantIds)
 */
data class Mission(
    val idMission: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    // timestamps in epoch millis for portability
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val status: MissionStatus = MissionStatus.PLANNED,
    val validationStatus: Status=Status.PENDING,
    val nbrMedecins: Int = 0,
    val nbrVolontaires: Int = 0,
    // One-to-many: task ids belonging to this mission
    val taskIds: List<String> = emptyList(),
    // Many-to-many: participation IDs linking this mission to users
    val participantIds: List<String> = emptyList(),
    // Photo (URL or storage path)
    val photoMission: String = "",
    // Full task objects (one-to-many). Stored here for simplicity in the in-memory implementation
    val listTasks: List<Task> = emptyList()
)