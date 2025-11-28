package com.example.healthconnect.data.models

import androidx.compose.ui.semantics.Role
import com.google.firebase.firestore.PropertyName
import com.example.healthconnect.data.models.Roles
import com.example.healthconnect.data.models.Specialite

/**
 * Représente la structure de données d'un utilisateur dans la base de données Firestore.
 * Un constructeur vide est nécessaire pour que Firestore puisse automatiquement mapper
 * un document en un objet de cette classe.
 */
data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    // @get:PropertyName est utilisé si le nom du champ dans Firestore est différent
    // du nom de la variable dans Kotlin. C'est une bonne pratique.
    @get:PropertyName("phoneNumber") @set:PropertyName("phoneNumber") var phoneNumber: String? = null,
    val role: Roles = Roles.VOLONTAIRE,

    val age: Int = 0,

    val adresse: String = "",

    val bio: String = "",

    val image: String = "",

    val statusRole: Status = Status.PENDING,

    // Specific fields for Doctors (Médecins)
    val cabinet: String = "",
    val specialiteMedecin: Specialite = Specialite.GENERALISTE,
    val carteService: String = "", // Assuming this is an image URL/path

    // Specific fields for Associations
    val association: String = "",
    val posteAsso: String = "",
    val carteAssociation: String = "", // Image URL/path for association card

    // Relations (storing IDs to avoid cycles)
    val missionIds: List<String> = emptyList(),         // Missions the user is linked to (via Participation)
    val participationIds: List<String> = emptyList(),   // Participation entries linking user <-> mission
    val assignedTaskIds: List<String> = emptyList()     // Tasks assigned to this user (ids)
)
