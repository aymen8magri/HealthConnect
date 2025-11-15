package com.example.healthconnect.data.mission
import javax.inject.Inject

class MissionRepositoryImpl @Inject constructor() : MissionRepository {

    private val missionsList = listOf(
        Mission(
            id = "1",
            title = "Campagne de Vaccination",
            description = "Vaccination anti-grippe au centre Ariana.",
            location = "Centre de santé Ariana",
            date = "2025-11-15",
            startTime = "09:00",
            endTime = "14:00",
            status = MissionStatus.PLANNED,
            // --- CHANGEMENTS APPLIQUÉS ---
            coordinatorId = "coord_amina_ben_ali", // ID au lieu du nom
            volunteers = listOf("user_101", "user_102", "user_103"), // Liste d'IDs de volontaires
            doctors = listOf("doc_201", "doc_202"), // Liste d'IDs de docteurs
            maxVolunteers = 10,
            maxDoctors = 5,
            materialRequired = listOf("Gants", "Seringues", "Glacières"),
            imageUrl = "https://picsum.photos/seed/vaccination/400/300"
        ),
        Mission(
            id = "2",
            title = "Dépistage Diabète",
            description = "Dépistage pour les familles du quartier.",
            location = "Maison de Jeunes El Ghazela",
            date = "2025-11-18",
            startTime = "10:00",
            endTime = "16:00",
            status = MissionStatus.IN_PROGRESS,
            // --- CHANGEMENTS APPLIQUÉS ---
            coordinatorId = "coord_samir_trabelsi",
            volunteers = listOf("user_104", "user_105"),
            doctors = listOf("doc_203"),
            maxVolunteers = 12,
            maxDoctors = 6,
            materialRequired = listOf("Glucomètres", "Lingettes"),
            imageUrl = null // Test avec pas d'image
        ),
        Mission(
            id = "3",
            title = "Collecte de Sang",
            description = "Collecte annuelle au centre ville.",
            location = "Hôpital Principal",
            date = "2025-11-20",
            startTime = "08:00",
            endTime = "13:00",
            status = MissionStatus.CANCELLED,
            // --- CHANGEMENTS APPLIQUÉS ---
            coordinatorId = "coord_khaled_mourad",
            volunteers = emptyList(), // Personne n'a eu le temps de s'inscrire
            doctors = listOf("doc_204", "doc_205", "doc_206"),
            maxVolunteers = 15,
            maxDoctors = 6,
            materialRequired = listOf("Trousse de premiers secours", "Gants"),
            imageUrl = "https://picsum.photos/seed/collecte/400/300"
        ),
        Mission(
            id = "4",
            title = "Campagne de Sensibilisation COVID-19",
            description = "Distribution de flyers et masques dans le quartier.",
            location = "Quartier El Menzah",
            date = "2025-11-22",
            startTime = "09:00",
            endTime = "12:00",
            status = MissionStatus.COMPLETED,
            // --- CHANGEMENTS APPLIQUÉS ---
            coordinatorId = "coord_leila_saidi",
            volunteers = listOf("user_106", "user_107", "user_108", "user_109", "user_110"),
            doctors = listOf("doc_207"),
            maxVolunteers = 20,
            maxDoctors = 6,
            materialRequired = listOf("Masques", "Flyers", "Gel Hydroalcoolique"),
            imageUrl = "https://picsum.photos/seed/covid/400/300"
        )
    )

    override suspend fun getAllMissions(): List<Mission> {
        // TODO: Remplacer plus tard par API
        return missionsList
    }

    override suspend fun getMissionById(missionId: String): Mission? {
        // Simule une recherche ciblée dans la base de données
        // Trouve la première mission dont l'ID correspond et la retourne.
        return missionsList.find { it.id == missionId }
    }
}
