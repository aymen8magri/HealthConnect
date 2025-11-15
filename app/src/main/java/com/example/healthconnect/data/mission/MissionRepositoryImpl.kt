package com.example.healthconnect.data.mission
import javax.inject.Inject

class MissionRepositoryImpl @Inject constructor() : MissionRepository {

    override suspend fun getAllMissions(): List<Mission> {
        // TODO: Remplacer plus tard par API
        return listOf(
            Mission(
                id = "1",
                title = "Campagne de Vaccination",
                description = "Vaccination anti-grippe au centre Ariana.",
                location = "Centre de santé Ariana",
                date = "2025-11-15",
                startTime = "09:00",
                endTime = "14:00",
                status = MissionStatus.PLANNED,
                coordinatorName = "Dr. Amina Ben Ali",
                volunteersCount = 5,
                maxVolunteers = 10,
                materialRequired = listOf("Gants", "Seringues", "Glacières"),
                imageUrl = "https://picsum.photos/seed/marche/400/300"
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
                coordinatorName = "Inf. Samir Trabelsi",
                volunteersCount = 8,
                maxVolunteers = 12,
                materialRequired = listOf("Glucomètres", "Lingettes"),
                //imageUrl = "https://picsum.photos/seed/marche/400/300"
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
                coordinatorName = "Dr. Khaled Mourad",
                volunteersCount = 10,
                maxVolunteers = 15,
                materialRequired = listOf("Trousse de premiers secours", "Gants"),
                imageUrl = "https://picsum.photos/seed/marche/400/300"
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
                coordinatorName = "Dr. Leila Saidi",
                volunteersCount = 12,
                maxVolunteers = 20,
                materialRequired = listOf("Masques", "Flyers", "Gel Hydroalcoolique"),
                imageUrl = "https://picsum.photos/seed/marche/400/300"
            ),
            Mission(
                id = "5",
                title = "Collecte de Déchets Médicaux",
                description = "Ramassage des déchets dans les hôpitaux locaux.",
                location = "Hôpital Charles Nicolle",
                date = "2025-11-25",
                startTime = "07:00",
                endTime = "11:00",
                status = MissionStatus.PLANNED,
                coordinatorName = "Dr. Sami Jaziri",
                volunteersCount = 6,
                maxVolunteers = 10,
                materialRequired = listOf("Gants", "Sacs Spéciaux"),
                //imageUrl = "https://picsum.photos/seed/marche/400/300"
            ),
            Mission(
                id = "6",
                title = "Campagne de Don de Vêtements",
                description = "Collecte et distribution de vêtements pour familles défavorisées.",
                location = "Centre Communautaire El Omrane",
                date = "2025-11-28",
                startTime = "10:00",
                endTime = "16:00",
                status = MissionStatus.IN_PROGRESS,
                coordinatorName = "Mme. Sana Triki",
                volunteersCount = 15,
                maxVolunteers = 25,
                materialRequired = listOf("Vêtements", "Sacs", "Étiquettes"),
                imageUrl = "https://picsum.photos/seed/marche/400/300"
            )
        )

    }
}
