package com.alperen.spendcraft.shared.domain.calculator

import com.alperen.spendcraft.shared.domain.model.Streak
import kotlinx.datetime.*

/**
 * Streak calculation engine
 * Platform-agnostic logic for daily streak tracking
 */
object StreakCalculator {
    
    /**
     * Calculate streak from transaction timestamps
     * iOS/Android pattern: Counts consecutive days with at least one transaction
     */
    fun calculateFromTransactions(
        transactionTimestamps: List<Long>,
        nowMillis: Long = Clock.System.now().toEpochMilliseconds()
    ): Streak {
        if (transactionTimestamps.isEmpty()) {
            return Streak(current = 0, best = 0)
        }
        
        // Convert to dates (removing time component)
        val dates = transactionTimestamps
            .map { getDateOnly(it) }
            .distinct()
            .sorted()
            .reversed() // Most recent first
        
        var currentStreak = 0
        var bestStreak = 0
        var tempStreak = 0
        
        val today = getDateOnly(nowMillis)
        
        dates.forEachIndexed { index, date ->
            if (index == 0) {
                // First date
                tempStreak = 1
                
                // Only count as current if it's today or yesterday
                if (date == today || date == today - 86_400_000) {
                    currentStreak = 1
                }
            } else {
                val prevDate = dates[index - 1]
                val diffDays = (prevDate - date) / 86_400_000
                
                if (diffDays == 1L) {
                    // Consecutive day
                    tempStreak++
                    
                    if (currentStreak > 0) {
                        currentStreak++
                    }
                } else {
                    // Streak broken
                    bestStreak = maxOf(bestStreak, tempStreak)
                    tempStreak = 1
                    currentStreak = 0
                }
            }
        }
        
        bestStreak = maxOf(bestStreak, tempStreak)
        
        return Streak(
            current = currentStreak,
            best = bestStreak
        )
    }
    
    /**
     * Check if streak is active (logged today or yesterday)
     */
    fun isStreakActive(
        lastTransactionMillis: Long,
        nowMillis: Long = Clock.System.now().toEpochMilliseconds()
    ): Boolean {
        val lastDate = getDateOnly(lastTransactionMillis)
        val today = getDateOnly(nowMillis)
        val yesterday = today - 86_400_000
        
        return lastDate == today || lastDate == yesterday
    }
    
    /**
     * Check if logged today
     */
    fun isLoggedToday(
        transactionTimestamps: List<Long>,
        nowMillis: Long = Clock.System.now().toEpochMilliseconds()
    ): Boolean {
        val today = getDateOnly(nowMillis)
        
        return transactionTimestamps.any { getDateOnly(it) == today }
    }
    
    /**
     * Get date only (midnight timestamp)
     * Removes time component for day comparison
     */
    private fun getDateOnly(timestampMillis: Long): Long {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val timeZone = TimeZone.currentSystemDefault()
        val localDate = instant.toLocalDateTime(timeZone).date
        val startOfDay = localDate.atStartOfDayIn(timeZone)
        return startOfDay.toEpochMilliseconds()
    }
    
    /**
     * Calculate current streak from daily entries (iOS compatibility)
     */
    fun calculateCurrentStreak(
        dailyEntries: List<Instant>,
        today: Instant
    ): Int {
        if (dailyEntries.isEmpty()) {
            return 0
        }
        
        val todayDate = today.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val sortedEntries = dailyEntries
            .map { it.toLocalDateTime(TimeZone.currentSystemDefault()).date }
            .distinct()
            .sorted()
            .reversed()
        
        var streak = 0
        var currentDate = todayDate
        
        for (entryDate in sortedEntries) {
            val diffDays = (currentDate.toEpochDays() - entryDate.toEpochDays()).toInt()
            
            if (diffDays == streak) {
                streak++
                currentDate = entryDate.minus(1, DateTimeUnit.DAY)
            } else {
                break
            }
        }
        
        return streak
    }
}




