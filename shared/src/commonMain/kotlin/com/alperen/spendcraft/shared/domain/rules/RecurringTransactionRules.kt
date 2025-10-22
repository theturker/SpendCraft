package com.alperen.spendcraft.shared.domain.rules

import kotlinx.datetime.*

/**
 * Recurring transaction business rules
 * Platform-agnostic logic for recurring transactions
 */
object RecurringTransactionRules {
    
    enum class Frequency {
        DAILY,
        WEEKLY,
        BIWEEKLY,
        MONTHLY,
        YEARLY;
        
        fun toDays(): Int = when (this) {
            DAILY -> 1
            WEEKLY -> 7
            BIWEEKLY -> 14
            MONTHLY -> 30 // Approximate
            YEARLY -> 365
        }
    }
    
    /**
     * Calculate next occurrence of recurring transaction
     * 
     * @param lastOccurrenceMillis Last occurrence timestamp
     * @param frequency Recurrence frequency
     * @return Next occurrence timestamp in milliseconds
     */
    fun calculateNextOccurrence(
        lastOccurrenceMillis: Long,
        frequency: Frequency
    ): Long {
        val lastInstant = Instant.fromEpochMilliseconds(lastOccurrenceMillis)
        val timeZone = TimeZone.currentSystemDefault()
        val lastDateTime = lastInstant.toLocalDateTime(timeZone)
        
        val nextDateTime = when (frequency) {
            Frequency.DAILY -> lastDateTime.date.plus(1, DateTimeUnit.DAY)
            Frequency.WEEKLY -> lastDateTime.date.plus(7, DateTimeUnit.DAY)
            Frequency.BIWEEKLY -> lastDateTime.date.plus(14, DateTimeUnit.DAY)
            Frequency.MONTHLY -> lastDateTime.date.plus(1, DateTimeUnit.MONTH)
            Frequency.YEARLY -> lastDateTime.date.plus(1, DateTimeUnit.YEAR)
        }
        
        // Keep same time of day
        val nextInstant = nextDateTime.atTime(lastDateTime.hour, lastDateTime.minute)
            .toInstant(timeZone)
        
        return nextInstant.toEpochMilliseconds()
    }
    
    /**
     * Check if recurring transaction should trigger now
     * 
     * @param lastOccurrenceMillis Last time transaction was created
     * @param frequency Recurrence frequency
     * @param nowMillis Current timestamp (for testing purposes)
     * @return true if should create new transaction
     */
    fun shouldTrigger(
        lastOccurrenceMillis: Long,
        frequency: Frequency,
        nowMillis: Long = Clock.System.now().toEpochMilliseconds()
    ): Boolean {
        val nextOccurrence = calculateNextOccurrence(lastOccurrenceMillis, frequency)
        return nowMillis >= nextOccurrence
    }
    
    /**
     * Get all missed occurrences
     * Returns list of timestamps that should have been triggered but weren't
     */
    fun getMissedOccurrences(
        lastOccurrenceMillis: Long,
        frequency: Frequency,
        nowMillis: Long = Clock.System.now().toEpochMilliseconds()
    ): List<Long> {
        val missed = mutableListOf<Long>()
        var nextOccurrence = calculateNextOccurrence(lastOccurrenceMillis, frequency)
        
        // Maximum 100 occurrences to prevent infinite loops
        var count = 0
        while (nextOccurrence < nowMillis && count < 100) {
            missed.add(nextOccurrence)
            nextOccurrence = calculateNextOccurrence(nextOccurrence, frequency)
            count++
        }
        
        return missed
    }
    
    /**
     * Calculate total occurrences in time range
     */
    fun calculateOccurrencesInRange(
        startMillis: Long,
        endMillis: Long,
        frequency: Frequency
    ): Int {
        val daysDiff = ((endMillis - startMillis) / 86_400_000).toInt()
        val frequencyDays = frequency.toDays()
        
        return daysDiff / frequencyDays
    }
}




