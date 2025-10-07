package com.alperen.spendcraft.shared.domain.repository

import com.alperen.spendcraft.shared.domain.model.Streak
import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    fun observeStreak(): Flow<Streak>
    suspend fun markTodayLogged()
}

