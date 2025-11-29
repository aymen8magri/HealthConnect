package com.example.healthconnect.dependanciesInjections

import com.example.healthconnect.data.auth.login.LoginRepository
import com.example.healthconnect.data.auth.login.LoginRepositoryImpl
import com.example.healthconnect.data.auth.register.RegisterRepository
import com.example.healthconnect.data.auth.register.RegisterRepositoryImpl
import com.example.healthconnect.data.mission.MissionRepository
import com.example.healthconnect.data.mission.MissionRepositoryImpl
import com.example.healthconnect.data.admin.validateUsers.UserRepository
import com.example.healthconnect.data.admin.validateUsers.UserRepositoryImpl
import com.example.healthconnect.data.participation.ParticipationRepository
import com.example.healthconnect.data.participation.ParticipationRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import javax.inject.Singleton
import dagger.hilt.components.SingletonComponent // <-- VÉRIFIEZ CET IMPORT
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideParticipationRepository(firestore: FirebaseFirestore): ParticipationRepository {
        return ParticipationRepositoryImpl(firestore)
    }

    // Fournit une instance de FirebaseAuth pour toute l'application
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    // Fournit une instance de FirebaseFirestore pour toute l'application
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideMissionRepository(firestore: FirebaseFirestore): MissionRepository {
        return MissionRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore
    ): UserRepository {
        return UserRepositoryImpl(firestore)
    }

    // Fournit une instance de RegisterRepository
    @Provides
    @Singleton
    fun provideRegisterRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): RegisterRepository {
        // On passe les instances dont RegisterRepositoryImpl a besoin
        return RegisterRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth
    ): LoginRepository {
        // Hilt va fournir l'instance de FirebaseAuth automatiquement
        return LoginRepositoryImpl(firebaseAuth)
    }
}
