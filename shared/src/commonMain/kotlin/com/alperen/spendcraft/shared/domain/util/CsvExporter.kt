package com.alperen.spendcraft.shared.domain.util

import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.model.TransactionType
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CsvExporter {

    fun exportTransactions(transactions: List<Transaction>): String {
        val header = "Date,Amount,Description,Category,Type"
        val rows = transactions.map { transaction ->
            formatTransactionForCsv(transaction)
        }
        
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun formatTransactionForCsv(transaction: Transaction): String {
        val date = formatDate(transaction.timestampUtcMillis)
        val amount = formatAmount(transaction.amount.minorUnits)
        val description = escapeCsvField(transaction.note ?: "")
        val category = escapeCsvField(transaction.categoryId?.toString() ?: "Diğer")
        val type = if (transaction.type == TransactionType.INCOME) "Gelir" else "Gider"
        
        return "$date,$amount,$description,$category,$type"
    }

    private fun formatDate(timestampMillis: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.UTC)
        return "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')}"
    }

    private fun formatAmount(amountMinor: Long): String {
        val amount = amountMinor / 100.0
        // KMP-compatible string formatting
        val intPart = amount.toLong()
        val fracPart = ((amount - intPart) * 100).toInt()
        return "$intPart.${fracPart.toString().padStart(2, '0')}"
    }

    private fun escapeCsvField(field: String): String {
        return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
    }
}

