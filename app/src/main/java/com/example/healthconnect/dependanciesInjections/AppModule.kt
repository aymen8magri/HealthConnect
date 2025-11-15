package com.example.healthconnect.dependanciesInjections

import com.example.healthconnect.data.mission.MissionRepository
import com.example.healthconnect.data.mission.MissionRepositoryImpl
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
    fun provideMissionRepository(): MissionRepository {
        return MissionRepositoryImpl()
    }
}
