package com.alperen.spendcraft.data.repository

import com.alperen.spendcraft.core.model.Streak
import com.alperen.spendcraft.data.db.dao.DailyEntryDao
import com.alperen.spendcraft.data.db.entities.DailyEntryEntity
import com.alperen.spendcraft.domain.repo.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class StreakRepositoryImpl @Inject constructor(
    private val dailyEntryDao: DailyEntryDao
) : StreakRepository {

    override fun observeStreak(): Flow<Streak> {
        return dailyEntryDao.observeAll().map { entries ->
            calculateStreak(entries)
        }
    }

    override suspend fun markTodayLogged() {
        val todayEpochDay = getCurrentEpochDay()
        val exists = dailyEntryDao.existsForDay(todayEpochDay) > 0
        
        if (!exists) {
            dailyEntryDao.insert(DailyEntryEntity(dateEpochDay = todayEpochDay))
        }
    }

    private fun calculateStreak(entries: List<DailyEntryEntity>): Streak {
        if (entries.isEmpty()) return Streak(0, 0)
        
        val sortedDays = entries.map { it.dateEpochDay }.sorted()
        val todayEpochDay = getCurrentEpochDay()
        
        // Calculate current streak
        var currentStreak = 0
        var checkDay = todayEpochDay
        
        // Check if today is logged
        if (sortedDays.contains(todayEpochDay)) {
            currentStreak = 1
            checkDay = todayEpochDay - 1
            
            // Count consecutive days backwards
            while (sortedDays.contains(checkDay)) {
                currentStreak++
                checkDay--
            }
        } else {
            // Check if yesterday is logged (grace period)
            checkDay = todayEpochDay - 1
            if (sortedDays.contains(checkDay)) {
                currentStreak = 1
                checkDay--
                
                while (sortedDays.contains(checkDay)) {
                    currentStreak++
                    checkDay--
                }
            }
        }
        
        // Calculate best streak
        val bestStreak = calculateBestStreak(sortedDays)
        
        return Streak(currentStreak, maxOf(currentStreak, bestStreak))
    }
    
    private fun calculateBestStreak(sortedDays: List<Int>): Int {
        if (sortedDays.isEmpty()) return 0
        
        var maxStreak = 1
        var currentStreak = 1
        
        for (i in 1 until sortedDays.size) {
            if (sortedDays[i] == sortedDays[i - 1] + 1) {
                currentStreak++
                maxStreak = maxOf(maxStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }
        
        return maxStreak
    }
    
    private fun getCurrentEpochDay(): Int {
        return Instant.now()
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .toEpochDay()
            .toInt()
    }
}

