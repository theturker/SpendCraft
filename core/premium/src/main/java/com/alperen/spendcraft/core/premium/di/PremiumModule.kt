package com.alperen.spendcraft.core.premium.di

import android.content.Context
import com.alperen.spendcraft.core.premium.PremiumStateDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PremiumModule {
    
    @Provides
    @Singleton
    fun providePremiumStateDataStore(
        @ApplicationContext context: Context
    ): PremiumStateDataStore {
        return PremiumStateDataStore(context)
    }
}
