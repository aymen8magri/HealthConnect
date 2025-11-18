package com.example.healthconnect.data.models

import com.google.firebase.firestore.PropertyName

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
    val role: String = ""
)
