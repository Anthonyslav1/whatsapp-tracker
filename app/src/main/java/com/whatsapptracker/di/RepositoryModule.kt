package com.whatsapptracker.di

import com.whatsapptracker.data.db.ChatSessionDao
import com.whatsapptracker.data.repository.UsageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUsageRepository(dao: ChatSessionDao): UsageRepository {
        return UsageRepository(dao)
    }
}
