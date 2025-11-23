package com.example.healthconnect.data.models

import com.google.firebase.Timestamp

data class Mission(
    var id: String = "",  // <-- ID Firestore ajouté
    var title: String? = null,
    var description: String? = null,
    var location: String? = null,
    var startDate: Timestamp? = null,
    var endDate: Timestamp? = null,
    var status: MissionStatus = MissionStatus.PLANNED,
    var validationStatus: Status = Status.PENDING,
    var nbrMedecins: Int? = null,
    var nbrVolontaires: Int? = null,
    var taskIds: List<String>? = null,
    var participantIds: List<String>? = null,
    var photoMission: String? = null,
    var listTasks: List<String>? = null
)
