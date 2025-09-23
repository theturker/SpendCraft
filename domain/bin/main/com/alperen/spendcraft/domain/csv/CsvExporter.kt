package com.alperen.spendcraft.domain.csv

import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvExporter @Inject constructor() {

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
        val instant = Instant.ofEpochMilli(timestampMillis)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return instant.atZone(ZoneOffset.UTC).format(formatter)
    }

    private fun formatAmount(amountMinor: Long): String {
        val amount = amountMinor / 100.0
        return String.format("%.2f", amount)
    }

    private fun escapeCsvField(field: String): String {
        return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
    }
}
