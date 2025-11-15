package com.example.healthconnect.data.mission

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: MissionStatus,
    val coordinatorName: String,
    val volunteersCount: Int,
    val maxVolunteers: Int,
    val materialRequired: List<String>,
    val imageUrl: String? = null,   // si image serveur
    //val imageRes: Int? = null       // si image locale (drawable)
)

enum class MissionStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
