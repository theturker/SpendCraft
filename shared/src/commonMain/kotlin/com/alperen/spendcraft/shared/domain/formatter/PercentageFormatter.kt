package com.alperen.spendcraft.shared.domain.formatter

/**
 * Percentage formatter
 * Used for budget progress, analytics, etc.
 */
object PercentageFormatter {
    
    /**
     * Format percentage with specified decimals
     * Example: 75.5%, 100.0%, 33.33%
     */
    fun format(value: Double, decimals: Int = 1): String {
        val formatted = formatDecimal(value, decimals)
        return "$formatted%"
    }
    
    /**
     * Format budget progress
     * @param current Current spent amount
     * @param total Budget limit
     * @return Formatted percentage (e.g., "75.5%")
     */
    fun formatProgress(current: Long, total: Long): String {
        if (total <= 0) return "0%"
        
        val percentage = (current.toDouble() / total * 100)
        return format(percentage, decimals = 1)
    }
    
    /**
     * Format ratio as percentage
     * @param numerator Numerator value
     * @param denominator Denominator value
     */
    fun formatRatio(numerator: Long, denominator: Long, decimals: Int = 1): String {
        if (denominator == 0L) return "0%"
        
        val percentage = (numerator.toDouble() / denominator * 100)
        return format(percentage, decimals)
    }
    
    /**
     * Format with color indicator
     * Returns tuple: (formatted, color)
     * - Green: 0-50%
     * - Yellow: 50-80%
     * - Orange: 80-100%
     * - Red: >100%
     */
    fun formatWithColor(percentage: Double): Pair<String, ProgressColor> {
        val color = when {
            percentage < 50 -> ProgressColor.GREEN
            percentage < 80 -> ProgressColor.YELLOW
            percentage < 100 -> ProgressColor.ORANGE
            else -> ProgressColor.RED
        }
        
        return format(percentage) to color
    }
    
    enum class ProgressColor {
        GREEN, YELLOW, ORANGE, RED
    }
    
    /**
     * Internal: Format double with decimals
     */
    private fun formatDecimal(value: Double, decimals: Int): String {
        if (decimals == 0) {
            return value.toLong().toString()
        }
        
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        
        val intPart = value.toLong()
        val fracPart = ((value - intPart) * multiplier).toLong()
        
        return if (decimals > 0) {
            "$intPart.${kotlin.math.abs(fracPart).toString().padStart(decimals, '0')}"
        } else {
            intPart.toString()
        }
    }
}

