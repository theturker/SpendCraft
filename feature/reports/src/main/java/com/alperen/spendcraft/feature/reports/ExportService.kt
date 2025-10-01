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
        return try {
            val filtered = filterTransactionsByDateRange(transactions, dateRange, customStart, customEnd)
            val fileName = "spendcraft_report_${System.currentTimeMillis()}.pdf"
            val mimeType = "application/pdf"

            val resolver = context.contentResolver

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloads, fileName)
                Uri.fromFile(file)
            }

            if (uri != null) {
                val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    resolver.openOutputStream(uri)
                } else {
                    FileOutputStream(File(uri.path))
                }

                outputStream?.use { stream ->
                    val pdf = android.graphics.pdf.PdfDocument()
                    val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
                    val page = pdf.startPage(pageInfo)
                    val canvas = page.canvas

                    // Arkaplan
                    canvas.drawColor(android.graphics.Color.WHITE)

                    val titlePaint = android.graphics.Paint().apply {
                        textSize = 18f
                        isFakeBoldText = true
                        color = android.graphics.Color.BLACK
                    }
                    val textPaint = android.graphics.Paint().apply {
                        textSize = 12f
                        color = android.graphics.Color.DKGRAY
                    }
                    val highlightPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#FFA500")
                    }

                    var y = 40

                    // Logo (resources/drawable/spendcraft_logo.png beklenir)
                    try {
                        val logoId = context.resources.getIdentifier("spendcraft_logo", "drawable", context.packageName)
                        if (logoId != 0) {
                            val bmp = android.graphics.BitmapFactory.decodeResource(context.resources, logoId)
                            val scaled = android.graphics.Bitmap.createScaledBitmap(bmp, 64, 64, true)
                            canvas.drawBitmap(scaled, 30f, (y - 30).toFloat(), null)
                        }
                    } catch (_: Exception) {}

                    canvas.drawText("SpendCraft - Finans Raporu", 110f, y.toFloat(), titlePaint)
                    y += 12
                    canvas.drawText("${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}", 110f, y.toFloat(), textPaint)
                    y += 20

                    // Özet kutusu
                    canvas.drawRect(30f, y.toFloat(), 565f, (y + 50).toFloat(), highlightPaint)
                    val totalIncome = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.minorUnits }
                    val totalExpense = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.minorUnits }
                    val net = totalIncome - totalExpense
                    val summary = "Gelir: ₺${String.format("%.2f", totalIncome / 100.0)}  |  Gider: ₺${String.format("%.2f", totalExpense / 100.0)}  |  Net: ₺${String.format("%.2f", net / 100.0)}"
                    canvas.drawText(summary, 40f, (y + 30).toFloat(), android.graphics.Paint().apply { color = android.graphics.Color.WHITE; textSize = 12f; isFakeBoldText = true })
                    y += 70

                    // AI benzeri özet (lokal analiz) – hızlı içgörü
                    val expenseCount = filtered.count { it.type == TransactionType.EXPENSE }
                    val incomeCount = filtered.count { it.type == TransactionType.INCOME }
                    val topCategory = filtered.filter { it.type == TransactionType.EXPENSE }
                        .groupBy { it.categoryId }
                        .maxByOrNull { (_, list) -> list.sumOf { it.amount.minorUnits } }
                        ?.key
                    val topCategoryName = categories.find { it.id == topCategory }?.name ?: "Bilinmeyen"
                    val insight = "${expenseCount} gider, ${incomeCount} gelir. En çok harcama: $topCategoryName."
                    canvas.drawText("Analiz: $insight", 30f, y.toFloat(), textPaint)
                    y += 20

                    canvas.drawText("Hesap Ekstresi", 30f, y.toFloat(), titlePaint)
                    y += 16

                    // Tablo başlıkları
                    canvas.drawText("Tarih", 30f, y.toFloat(), textPaint)
                    canvas.drawText("Tür", 120f, y.toFloat(), textPaint)
                    canvas.drawText("Kategori", 170f, y.toFloat(), textPaint)
                    canvas.drawText("Tutar", 350f, y.toFloat(), textPaint)
                    canvas.drawText("Açıklama", 430f, y.toFloat(), textPaint)
                    y += 12
                    canvas.drawLine(30f, y.toFloat(), 565f, y.toFloat(), android.graphics.Paint().apply { color = android.graphics.Color.LTGRAY })
                    y += 10

                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    filtered.forEach { tx ->
                        if (y > 800) return@forEach
                        val date = sdf.format(java.util.Date(tx.timestampUtcMillis))
                        val type = if (tx.type == TransactionType.INCOME) "+" else "-"
                        val amount = String.format("%.2f", tx.amount.minorUnits / 100.0)
                        val categoryName = categories.find { it.id == tx.categoryId }?.name ?: "Bilinmeyen"
                        val note = (tx.note ?: "").take(28)

                        canvas.drawText(date, 30f, y.toFloat(), textPaint)
                        canvas.drawText(type, 120f, y.toFloat(), textPaint)
                        canvas.drawText(categoryName.take(16), 170f, y.toFloat(), textPaint)
                        canvas.drawText("₺$amount", 350f, y.toFloat(), textPaint)
                        canvas.drawText(note, 430f, y.toFloat(), textPaint)
                        y += 16
                    }

                    pdf.finishPage(page)
                    pdf.writeTo(stream)
                    pdf.close()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // IS_PENDING=0 yap
                    val cvDone = android.content.ContentValues().apply {
                        put(MediaStore.MediaColumns.IS_PENDING, 0)
                    }
                    resolver.update(uri, cvDone, null, null)
                }
                uri
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
    
    fun shareFile(uri: Uri, mimeType: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Raporu Paylaş veya Kaydet"))
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: try to open file directly
            try {
                val openIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(openIntent)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    // Geriye dönük uyum: uzantıya bakarak tahmin et
    fun shareFile(uri: Uri) {
        val guessedMime = when {
            uri.toString().endsWith(".pdf", ignoreCase = true) -> "application/pdf"
            uri.toString().endsWith(".csv", ignoreCase = true) -> "text/csv"
            else -> "application/octet-stream"
        }
        shareFile(uri, guessedMime)
    }
}
