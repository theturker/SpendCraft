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
            android.util.Log.d("ReceiptAnalyzer", "Starting OCR analysis. Bitmap size: ${bitmap.width}x${bitmap.height}")
            val image = InputImage.fromBitmap(bitmap, 0)
            val text = textRecognizer.process(image).await()
            
            // ML Kit'ten gelen metni iOS'taki gibi işle
            // iOS'ta her observation'dan topCandidate alınıyor
            val recognizedStrings = mutableListOf<String>()
            for (block in text.textBlocks) {
                for (line in block.lines) {
                    recognizedStrings.add(line.text)
                }
            }
            
            // iOS'taki gibi newline ile birleştir
            val fullText = recognizedStrings.joinToString("\n")
            val lines = recognizedStrings
            
            android.util.Log.d("ReceiptAnalyzer", "OCR completed. Text length: ${fullText.length}, Lines: ${lines.size}")
            if (fullText.isNotEmpty()) {
                android.util.Log.d("ReceiptAnalyzer", "Raw OCR text (first 1000 chars): ${fullText.take(1000)}")
                android.util.Log.d("ReceiptAnalyzer", "All lines:")
                lines.forEachIndexed { index, line ->
                    android.util.Log.d("ReceiptAnalyzer", "  Line $index: '$line'")
                }
            } else {
                android.util.Log.w("ReceiptAnalyzer", "OCR returned empty text!")
            }
            
            val result = parseReceiptText(fullText, lines)
            
            android.util.Log.d("ReceiptAnalyzer", "Parse result - Amount: ${result.amount}, Merchant: '${result.merchant}', Date: ${result.date}, Items: ${result.items.size}")
            if (result.merchant != null) {
                android.util.Log.d("ReceiptAnalyzer", "Merchant found: '${result.merchant}'")
            } else {
                android.util.Log.w("ReceiptAnalyzer", "Merchant NOT found!")
            }
            if (result.amount != null) {
                android.util.Log.d("ReceiptAnalyzer", "Amount found: ${result.amount}")
            } else {
                android.util.Log.w("ReceiptAnalyzer", "Amount NOT found!")
            }
            
            result
        } catch (e: Exception) {
            android.util.Log.e("ReceiptAnalyzer", "OCR Error: ${e.message}", e)
            e.printStackTrace()
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
     * Parse amount string to Double - iOS ile tam uyumlu
     * iOS'ta: .replace(".", "").replace(",", ".") şeklinde yapılıyor
     */
    private fun parseAmountString(amountString: String?): Double? {
        if (amountString == null || amountString.isBlank()) return null
        
        // iOS'taki gibi: önce noktaları kaldır, sonra virgülü noktaya çevir
        // Bu Türkçe formatı (1.234,56) ve basit formatları (1234,56) handle eder
        val normalized = amountString
            .replace(".", "")
            .replace(",", ".")
        
        return normalized.toDoubleOrNull()
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
        
        // Pattern'leri önce tanımla (işyeri ve ürün bulma için kullanılacak)
        val amountPattern = Pattern.compile("[\\d,.]+\\s*(?:TL|TRY|₺)")
        val numberOnlyPattern = Pattern.compile("^[\\d,.]+$")
        
        // Tutar bulma - iOS ile tam uyumlu pattern'ler
        // Character class içinde nokta escape edilmez: [\\d,.] doğru, [\\d,\\.] yanlış
        val amountPatterns = listOf(
            Pattern.compile("TOPLAM[:\\s]*([\\d,.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("TOTAL[:\\s]*([\\d,.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("GENEL[:\\s]*TOPLAM[:\\s]*([\\d,.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("KDV[:\\s]*DAHİL[:\\s]*([\\d,.]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("([\\d,.]+)\\s*TL", Pattern.CASE_INSENSITIVE),
            Pattern.compile("([\\d,.]+)\\s*TRY", Pattern.CASE_INSENSITIVE),
            Pattern.compile("([\\d,.]+)\\s*₺", Pattern.CASE_INSENSITIVE),
            Pattern.compile("([\\d]{1,3}(?:\\.[\\d]{3})*(?:,[\\d]{2})?)\\s*(?:TL|TRY|₺)", Pattern.CASE_INSENSITIVE)
        )
        
        android.util.Log.d("ReceiptAnalyzer", "Searching for amount in text...")
        for (pattern in amountPatterns) {
            if (amount != null) break
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val amountString = matcher.group(1)
                android.util.Log.d("ReceiptAnalyzer", "Found amount string: '$amountString'")
                val parsed = parseAmountString(amountString)
                if (parsed != null && parsed > 0) {
                    amount = parsed
                    android.util.Log.d("ReceiptAnalyzer", "Parsed amount: $amount")
                    break
                }
            }
        }
        
        // Eğer direkt tutar bulunamazsa, en büyük sayıyı bul (iOS ile uyumlu)
        if (amount == null) {
            android.util.Log.d("ReceiptAnalyzer", "Direct amount not found, searching for largest number...")
            val numberPattern = Pattern.compile("([\\d]{1,3}(?:\\.[\\d]{3})*(?:,[\\d]{2})?)")
            val matcher = numberPattern.matcher(text)
            var maxAmount: Double? = null
            val allNumbers = mutableListOf<Double>()
            
            while (matcher.find()) {
                val amountString = matcher.group(1)
                val parsedAmount = parseAmountString(amountString)
                parsedAmount?.let {
                    allNumbers.add(it)
                    if (it > 0 && it < 1000000) { // Makul bir üst limit
                        if (maxAmount == null || it > maxAmount) {
                            maxAmount = it
                        }
                    }
                }
            }
            amount = maxAmount
            android.util.Log.d("ReceiptAnalyzer", "Found ${allNumbers.size} numbers, max: $maxAmount")
        }
        
        // İşyeri adı bulma - iOS ile tam uyumlu (çok basit mantık)
        // iOS'ta sadece ilk 3 satırdan birini alıyor, hiçbir filtreleme yok
        for ((index, line) in lines.take(5).withIndex()) {
            val trimmedLine = line.trim()
            // iOS'taki gibi: sadece uzunluk kontrolü, başka filtreleme yok
            if (trimmedLine.length > 3 && trimmedLine.length < 50) {
                // İlk birkaç satırdan birini işyeri olarak al (iOS'taki gibi - basit)
                if (index < 3 && merchant == null) {
                    merchant = trimmedLine
                    android.util.Log.d("ReceiptAnalyzer", "Found merchant at line $index: '$merchant'")
                    break
                }
            }
        }
        
        // Eğer hala bulunamadıysa, ilk anlamlı satırı al
        if (merchant == null) {
            for (line in lines.take(10)) {
                val trimmedLine = line.trim()
                if (trimmedLine.length > 3 && trimmedLine.length < 50) {
                    // Boş satırları, sadece sayı içeren satırları ve çok kısa satırları atla
                    if (!trimmedLine.matches(Regex("^[\\d\\s.,]+$")) && 
                        !trimmedLine.lowercase().contains("toplam") &&
                        !trimmedLine.lowercase().contains("total") &&
                        !trimmedLine.lowercase().contains("kdv")) {
                        merchant = trimmedLine
                        android.util.Log.d("ReceiptAnalyzer", "Found merchant (fallback): '$merchant'")
                        break
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
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.length > 2 && trimmedLine.length < 100) {
                // Tutar içermeyen satırları ürün olarak ekle
                val hasAmount = amountPattern.matcher(trimmedLine).find() ||
                        numberOnlyPattern.matcher(trimmedLine).find() ||
                        trimmedLine.lowercase().contains("toplam") ||
                        trimmedLine.lowercase().contains("kdv") ||
                        trimmedLine.lowercase().contains("total") ||
                        trimmedLine.lowercase().contains("tarih") ||
                        trimmedLine.lowercase().contains("date")
                
                if (!hasAmount && trimmedLine != merchant) {
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

