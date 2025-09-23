package com.alperen.spendcraft.domain.repo

import com.alperen.spendcraft.core.model.Streak
import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    fun observeStreak(): Flow<Streak>
    suspend fun markTodayLogged()
}

