package com.alperen.spendcraft.feature.dashboard.ui

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Receipt analysis result - iOS ReceiptAnalysisResult benzeri
 */
data class ReceiptAnalysisResult(
    val amount: Double?,
    val merchant: String?,
    val date: Long?, // Timestamp in milliseconds
    val items: List<String>,
    val rawText: String
)

/**
 * Receipt Analyzer - iOS ReceiptAnalyzer.swift'in Android karşılığı
 * ML Kit Text Recognition kullanarak OCR yapar ve fiş bilgilerini parse eder
 */
object ReceiptAnalyzer {
    
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    /**
     * Analyze receipt image and extract information
     * iOS ReceiptAnalyzer.analyzeReceipt benzeri
     */
    suspend fun analyzeReceipt(bitmap: Bitmap): ReceiptAnalysisResult {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val text = textRecognizer.process(image).await()
            
            val fullText = text.text
            val lines = fullText.split("\n")
            
            val result = parseReceiptText(fullText, lines)
            result
        } catch (e: Exception) {
            android.util.Log.e("ReceiptAnalyzer", "OCR Error: ${e.message}", e)
            ReceiptAnalysisResult(
                amount = null,
                merchant = null,
                date = null,
                items = emptyList(),
                rawText = ""
            )
        }
    }
    
    /**
     * Parse receipt text to extract amount, merchant, date, and items
     * iOS ReceiptAnalyzer.parseReceiptText benzeri
     */
    private fun parseReceiptText(text: String, lines: List<String>): ReceiptAnalysisResult {
        var amount: Double? = null
        var merchant: String? = null
        var date: Long? = null
        val items = mutableListOf<String>()
        
        // Tutar bulma - Türkçe ve İngilizce formatlar
        val amountPatterns = listOf(
            Pattern.compile("TOPLAM[:\\s]*([\\d,\\.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("TOTAL[:\\s]*([\\d,\\.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("GENEL[:\\s]*TOPLAM[:\\s]*([\\d,\\.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("KDV[:\\s]*DAHİL[:\\s]*([\\d,\\.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("([\\d,\\.]+)\\s*TL", Pattern.CASE_INSENSITIVE),
            Pattern.compile("([\\d,\\.]+)\\s*TRY", Pattern.CASE_INSENSITIVE),
            Pattern.compile("([\\d,\\.]+)\\s*₺", Pattern.CASE_INSENSITIVE),
            Pattern.compile("([\\d]{1,3}(?:\\.[\\d]{3})*(?:,[\\d]{2})?)\\s*(?:TL|TRY|₺)", Pattern.CASE_INSENSITIVE)
        )
        
        for (pattern in amountPatterns) {
            if (amount != null) break
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val amountString = matcher.group(1)
                    ?.replace(".", "")
                    ?.replace(",", ".")
                amountString?.toDoubleOrNull()?.let {
                    amount = it
                }
            }
        }
        
        // Eğer direkt tutar bulunamazsa, en büyük sayıyı bul
        if (amount == null) {
            val numberPattern = Pattern.compile("([\\d]{1,3}(?:\\.[\\d]{3})*(?:,[\\d]{2})?)")
            val matcher = numberPattern.matcher(text)
            var maxAmount: Double? = null
            
            while (matcher.find()) {
                val amountString = matcher.group(1)
                    ?.replace(".", "")
                    ?.replace(",", ".")
                amountString?.toDoubleOrNull()?.let { parsedAmount ->
                    if (parsedAmount > 0) {
                        if (maxAmount == null || parsedAmount > maxAmount) {
                            maxAmount = parsedAmount
                        }
                    }
                }
            }
            amount = maxAmount
        }
        
        // İşyeri adı bulma - genellikle ilk satırlarda
        for ((index, line) in lines.take(5).withIndex()) {
            val trimmedLine = line.trim()
            if (trimmedLine.length > 3 && trimmedLine.length < 50) {
                // İlk birkaç satırdan birini işyeri olarak al
                if (index < 3 && merchant == null) {
                    // Büyük harflerle başlayan satırları tercih et
                    if (trimmedLine.firstOrNull()?.isUpperCase() == true) {
                        merchant = trimmedLine
                    }
                }
            }
        }
        
        // Tarih bulma
        val dateFormats = listOf(
            "dd.MM.yyyy",
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "yyyy-MM-dd",
            "dd MMMM yyyy",
            "dd MMM yyyy"
        )
        
        val locale = Locale("tr", "TR")
        for (format in dateFormats) {
            val dateFormatter = SimpleDateFormat(format, locale)
            for (line in lines) {
                try {
                    val parsedDate = dateFormatter.parse(line.trim())
                    if (parsedDate != null) {
                        date = parsedDate.time
                        break
                    }
                } catch (e: Exception) {
                    // Ignore parse errors
                }
            }
            if (date != null) break
        }
        
        // Ürün isimleri - genellikle tutar içermeyen satırlar
        val amountPattern = Pattern.compile("[\\d,\\.]+\\s*(?:TL|TRY|₺)")
        val numberOnlyPattern = Pattern.compile("^[\\d,\\.]+$")
        
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.length > 2 && trimmedLine.length < 100) {
                // Tutar içermeyen satırları ürün olarak ekle
                val hasAmount = amountPattern.matcher(trimmedLine).find() ||
                        numberOnlyPattern.matcher(trimmedLine).find() ||
                        trimmedLine.lowercase().contains("toplam") ||
                        trimmedLine.lowercase().contains("kdv")
                
                if (!hasAmount) {
                    items.add(trimmedLine)
                }
            }
        }
        
        return ReceiptAnalysisResult(
            amount = amount,
            merchant = merchant,
            date = date ?: System.currentTimeMillis(),
            items = items.take(10), // En fazla 10 ürün
            rawText = text
        )
    }
}

