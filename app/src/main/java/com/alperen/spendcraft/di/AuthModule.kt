package com.alperen.spendcraft.di

import com.alperen.spendcraft.analytics.FirebaseAnalyticsService
import com.alperen.spendcraft.analytics.FirebaseCrashlyticsService
import com.alperen.spendcraft.auth.FirebaseAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuthService(): FirebaseAuthService {
        return FirebaseAuthService()
    }
    
    @Provides
    @Singleton
    fun provideFirebaseAnalyticsService(
        @ApplicationContext context: android.content.Context
    ): FirebaseAnalyticsService {
        return FirebaseAnalyticsService(context)
    }
    
    @Provides
    @Singleton
    fun provideFirebaseCrashlyticsService(): FirebaseCrashlyticsService {
        return FirebaseCrashlyticsService()
    }
}
