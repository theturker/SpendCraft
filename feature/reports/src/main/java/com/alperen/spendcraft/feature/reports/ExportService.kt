package com.alperen.spendcraft.feature.reports

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.os.Build
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.model.TransactionType
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExportService(private val context: Context) {
    
    fun exportToCsv(
        transactions: List<Transaction>,
        categories: List<Category>,
        dateRange: com.alperen.spendcraft.feature.reports.DateRange,
        customStart: Long,
        customEnd: Long
    ): Uri? {
        return try {
            val fileName = "SpendCraft_Rapor_${getCurrentDateString()}.csv"
            
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ için MediaStore kullan
                val contentValues = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                // Android 9 ve altı için eski yöntem
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                Uri.fromFile(file)
            }
            
            if (uri != null) {
                val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver.openOutputStream(uri)
                } else {
                    FileOutputStream(File(uri.path))
                }
                
                outputStream?.use { stream ->
                    val writer = stream.bufferedWriter()
                    
                    // CSV Header
                    writer.appendLine("Tarih,Tür,Kategori,Miktar,Açıklama")
                    
                    // Filter transactions by date range
                    val filteredTransactions = filterTransactionsByDateRange(transactions, dateRange, customStart, customEnd)
                    
                    // Write transaction data
                    filteredTransactions.forEach { transaction ->
                        val categoryName = categories.find { it.id == transaction.categoryId }?.name ?: "Bilinmeyen"
                        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(transaction.timestampUtcMillis))
                        val type = if (transaction.type == TransactionType.INCOME) "Gelir" else "Gider"
                        val amount = String.format("%.2f", transaction.amount.minorUnits / 100.0)
                        
                        writer.appendLine("$date,$type,$categoryName,$amount,${transaction.note ?: ""}")
                    }
                    
                    writer.flush()
                }
                
                uri
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun exportToPdf(
        transactions: List<Transaction>,
        categories: List<Category>,
        dateRange: com.alperen.spendcraft.feature.reports.DateRange,
        customStart: Long,
        customEnd: Long
    ): Uri? {
        // TODO: Implement PDF export using a library like iText or similar
        // For now, return null to indicate not implemented
        return null
    }
    
    fun exportToExcel(
        transactions: List<Transaction>,
        categories: List<Category>,
        dateRange: com.alperen.spendcraft.feature.reports.DateRange,
        customStart: Long,
        customEnd: Long
    ): Uri? {
        // TODO: Implement Excel export using a library like Apache POI
        // For now, return null to indicate not implemented
        return null
    }
    
    private fun filterTransactionsByDateRange(
        transactions: List<Transaction>,
        dateRange: com.alperen.spendcraft.feature.reports.DateRange,
        customStart: Long,
        customEnd: Long
    ): List<Transaction> {
        val (startTime, endTime) = when (dateRange) {
            com.alperen.spendcraft.feature.reports.DateRange.LAST_WEEK -> {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                val start = calendar.timeInMillis
                val end = System.currentTimeMillis()
                Pair(start, end)
            }
            com.alperen.spendcraft.feature.reports.DateRange.LAST_MONTH -> {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MONTH, -1)
                val start = calendar.timeInMillis
                val end = System.currentTimeMillis()
                Pair(start, end)
            }
            com.alperen.spendcraft.feature.reports.DateRange.LAST_3_MONTHS -> {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MONTH, -3)
                val start = calendar.timeInMillis
                val end = System.currentTimeMillis()
                Pair(start, end)
            }
            com.alperen.spendcraft.feature.reports.DateRange.LAST_YEAR -> {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.YEAR, -1)
                val start = calendar.timeInMillis
                val end = System.currentTimeMillis()
                Pair(start, end)
            }
            com.alperen.spendcraft.feature.reports.DateRange.CUSTOM -> {
                Pair(customStart, customEnd)
            }
        }
        
        return transactions.filter { transaction ->
            transaction.timestampUtcMillis >= startTime && transaction.timestampUtcMillis <= endTime
        }
    }
    
    private fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }
    
    fun shareFile(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Raporu Paylaş veya Kaydet"))
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: try to open file directly
            try {
                val openIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "text/csv")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(openIntent)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }
}
