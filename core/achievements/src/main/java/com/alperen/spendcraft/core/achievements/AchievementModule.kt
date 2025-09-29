package com.alperen.spendcraft.core.achievements

import com.alperen.spendcraft.domain.achievements.AchievementManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AchievementModule {
    
    @Binds
    @Singleton
    abstract fun bindAchievementManager(
        achievementManagerImpl: AchievementManagerImpl
    ): AchievementManager
}
