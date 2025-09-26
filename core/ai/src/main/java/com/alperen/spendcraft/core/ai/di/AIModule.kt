package com.alperen.spendcraft.core.ai.di

import android.content.Context
import com.alperen.spendcraft.core.ai.AIKeyManager
import com.alperen.spendcraft.core.ai.GroqClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIModule {
    
    @Provides
    @Singleton
    fun provideGroqClient(): GroqClient {
        return GroqClient()
    }
    
    @Provides
    @Singleton
    fun provideAIKeyManager(@ApplicationContext context: Context): AIKeyManager {
        return AIKeyManager(context)
    }
}
