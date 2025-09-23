package com.alperen.spendcraft.domain.csv

import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.Money
import com.alperen.spendcraft.core.model.TransactionType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvParser @Inject constructor() {

    fun parseTransactions(csvContent: String): List<CsvTransaction> {
        val lines = csvContent.trim().split("\n")
        if (lines.isEmpty()) return emptyList()

        val header = lines[0].split(",").map { it.trim().removeSurrounding("\"") }
        val dataLines = lines.drop(1)

        return dataLines.mapNotNull { line ->
            try {
                parseTransactionLine(line, header)
            } catch (e: Exception) {
                null // Skip invalid lines
            }
        }
    }

    private fun parseTransactionLine(line: String, header: List<String>): CsvTransaction? {
        val values = parseCsvLine(line)
        if (values.size != header.size) return null

        val data = header.zip(values).toMap()

        val amount = parseAmount(data["amount"] ?: data["Amount"] ?: return null)
        val description = data["description"] ?: data["Description"] ?: data["note"] ?: data["Note"] ?: ""
        val category = data["category"] ?: data["Category"] ?: "Other"
        val date = parseDate(data["date"] ?: data["Date"] ?: return null)
        val isIncome = parseBoolean(data["isIncome"] ?: data["is_income"] ?: data["type"] ?: "false")

        return CsvTransaction(
            amount = amount,
            description = description,
            category = category,
            date = date,
            isIncome = isIncome
        )
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val char = line[i]
            when {
                char == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++ // Skip next quote
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                char == ',' && !inQuotes -> {
                    result.add(current.toString().trim())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
            i++
        }
        result.add(current.toString().trim())
        return result
    }

    private fun parseAmount(amountStr: String): Long {
        val cleanAmount = amountStr.replace(Regex("[^0-9.,]"), "")
        val normalizedAmount = cleanAmount.replace(",", ".")
        val amount = normalizedAmount.toDoubleOrNull() ?: 0.0
        return (amount * 100).toLong() // Convert to minor units
    }

    private fun parseDate(dateStr: String): Long {
        val formatters = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
        )

        for (formatter in formatters) {
            try {
                val localDate = LocalDate.parse(dateStr.trim(), formatter)
                return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            } catch (e: Exception) {
                continue
            }
        }
        
        // If no format works, use current time
        return Instant.now().toEpochMilli()
    }

    private fun parseBoolean(value: String): Boolean {
        return when (value.lowercase().trim()) {
            "true", "1", "yes", "income", "gelir" -> true
            else -> false
        }
    }
}

data class CsvTransaction(
    val amount: Long,
    val description: String,
    val category: String,
    val date: Long,
    val isIncome: Boolean
) {
    fun toTransaction(): Transaction {
        return Transaction(
            id = null,
            amount = Money(amount),
            timestampUtcMillis = date,
            note = description,
            categoryId = null, // Will be resolved by category name
            accountId = null,
            type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE
        )
    }
}
