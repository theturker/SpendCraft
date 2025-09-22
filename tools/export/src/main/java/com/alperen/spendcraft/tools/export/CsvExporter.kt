package com.alperen.spendcraft.tools.export

import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType

object CsvExporter {
    fun export(transactions: List<Transaction>): String {
        val header = "id,amount_minor,timestamp_utc_millis,note,category_id,type"
        val rows = transactions.joinToString("\n") { tx ->
            listOf(
                tx.id?.toString() ?: "",
                tx.amount.minorUnits.toString(),
                tx.timestampUtcMillis.toString(),
                (tx.note ?: "").escapeCsv(),
                tx.categoryId?.toString() ?: "",
                if (tx.type == TransactionType.INCOME) "INCOME" else "EXPENSE"
            ).joinToString(",")
        }
        return if (rows.isEmpty()) header else "$header\n$rows"
    }

    private fun String.escapeCsv(): String {
        val needsQuotes = contains(",") || contains("\"") || contains("\n")
        val escaped = replace("\"", "\"\"")
        return if (needsQuotes) "\"$escaped\"" else escaped
    }
}




