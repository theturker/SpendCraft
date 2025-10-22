package com.alperen.spendcraft.shared.domain.formatter

import kotlinx.datetime.*

/**
 * Platform-agnostic date/time formatter
 * 
 * Features (Android + iOS combined):
 * - Multiple date formats (SHORT, MEDIUM, LONG)
 * - Relative time (Türkçe: "2 dakika önce", "3 saat önce")
 * - Locale-aware (tr_TR)
 * - Timezone support
 */
object DateTimeFormatter {
    
    enum class DateFormat {
        SHORT,      // 21.10.2024
        MEDIUM,     // 21 Ekim 2024
        LONG,       // Pazartesi, 21 Ekim 2024
        TIME,       // 14:30
        DATETIME    // 21 Ekim 2024, 14:30
    }
    
    private val monthNamesTR = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )
    
    private val dayNamesTR = listOf(
        "Pazartesi", "Salı", "Çarşamba", "Perşembe", 
        "Cuma", "Cumartesi", "Pazar"
    )
    
    /**
     * Format date according to specified format
     * iOS pattern: "d MMMM yyyy" → "21 Ekim 2024"
     */
    fun format(
        timestampMillis: Long,
        format: DateFormat = DateFormat.MEDIUM,
        locale: String = "tr_TR"
    ): String {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val timeZone = TimeZone.currentSystemDefault()
        val localDateTime = instant.toLocalDateTime(timeZone)
        
        return when (format) {
            DateFormat.SHORT -> formatShort(localDateTime)
            DateFormat.MEDIUM -> formatMedium(localDateTime)
            DateFormat.LONG -> formatLong(localDateTime)
            DateFormat.TIME -> formatTime(localDateTime)
            DateFormat.DATETIME -> formatDateTime(localDateTime)
        }
    }
    
    /**
     * Format: 21.10.2024
     */
    private fun formatShort(dateTime: LocalDateTime): String {
        return "${dateTime.dayOfMonth}.${dateTime.monthNumber}.${dateTime.year}"
    }
    
    /**
     * Format: 21 Ekim 2024 (iOS pattern)
     */
    private fun formatMedium(dateTime: LocalDateTime): String {
        val monthName = monthNamesTR[dateTime.monthNumber - 1]
        return "${dateTime.dayOfMonth} $monthName ${dateTime.year}"
    }
    
    /**
     * Format: Pazartesi, 21 Ekim 2024
     */
    private fun formatLong(dateTime: LocalDateTime): String {
        val dayName = getDayName(dateTime.dayOfWeek)
        val monthName = monthNamesTR[dateTime.monthNumber - 1]
        return "$dayName, ${dateTime.dayOfMonth} $monthName ${dateTime.year}"
    }
    
    /**
     * Format: 14:30
     */
    private fun formatTime(dateTime: LocalDateTime): String {
        return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
    }
    
    /**
     * Format: 21 Ekim 2024, 14:30
     */
    private fun formatDateTime(dateTime: LocalDateTime): String {
        return "${formatMedium(dateTime)}, ${formatTime(dateTime)}"
    }
    
    /**
     * Format relative time (iOS pattern)
     * Examples:
     * - "Az önce" (< 1 min)
     * - "5 dakika önce"
     * - "2 saat önce"
     * - "3 gün önce"
     * - "21 Ekim 2024" (> 7 days)
     */
    fun formatRelative(timestampMillis: Long): String {
        val now = Clock.System.now().toEpochMilliseconds()
        val diff = now - timestampMillis
        
        return when {
            diff < 0 -> formatMedium(Instant.fromEpochMilliseconds(timestampMillis).toLocalDateTime(TimeZone.currentSystemDefault()))
            diff < 60_000 -> "Az önce" // < 1 minute
            diff < 3_600_000 -> "${diff / 60_000} dakika önce" // < 1 hour
            diff < 86_400_000 -> "${diff / 3_600_000} saat önce" // < 1 day
            diff < 604_800_000 -> "${diff / 86_400_000} gün önce" // < 1 week
            else -> format(timestampMillis, DateFormat.MEDIUM) // > 1 week
        }
    }
    
    /**
     * Check if date is today
     */
    fun isToday(timestampMillis: Long): Boolean {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        return dateTime.year == now.year &&
               dateTime.monthNumber == now.monthNumber &&
               dateTime.dayOfMonth == now.dayOfMonth
    }
    
    /**
     * Check if date is this week
     */
    fun isThisWeek(timestampMillis: Long): Boolean {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val now = Clock.System.now()
        val diff = now.toEpochMilliseconds() - instant.toEpochMilliseconds()
        
        return diff >= 0 && diff < 604_800_000 // 7 days
    }
    
    /**
     * Check if date is this month
     */
    fun isThisMonth(timestampMillis: Long): Boolean {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        return dateTime.year == now.year && dateTime.monthNumber == now.monthNumber
    }
    
    /**
     * Get day name in Turkish
     */
    private fun getDayName(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> dayNamesTR[0]
            DayOfWeek.TUESDAY -> dayNamesTR[1]
            DayOfWeek.WEDNESDAY -> dayNamesTR[2]
            DayOfWeek.THURSDAY -> dayNamesTR[3]
            DayOfWeek.FRIDAY -> dayNamesTR[4]
            DayOfWeek.SATURDAY -> dayNamesTR[5]
            DayOfWeek.SUNDAY -> dayNamesTR[6]
            else -> ""
        }
    }
    
    /**
     * Get start of day timestamp
     */
    fun startOfDay(timestampMillis: Long): Long {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startOfDay = localDate.atStartOfDayIn(TimeZone.currentSystemDefault())
        return startOfDay.toEpochMilliseconds()
    }
    
    /**
     * Get end of day timestamp
     */
    fun endOfDay(timestampMillis: Long): Long {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val nextDay = localDate.plus(1, DateTimeUnit.DAY)
        val endOfDay = nextDay.atStartOfDayIn(TimeZone.currentSystemDefault())
        return endOfDay.toEpochMilliseconds() - 1
    }
    
    /**
     * Convert platform Date to Instant (iOS compatibility)
     */
    fun dateToInstant(date: Any): Instant {
        // This is a placeholder - actual implementation would depend on platform
        // For now, return current time
        return Clock.System.now()
    }
}




