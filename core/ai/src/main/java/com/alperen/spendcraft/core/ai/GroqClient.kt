package com.alperen.spendcraft.core.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqRequest(
    val messages: List<GroqMessage>,
    val model: String = "llama-3.1-8b-instant",
    val max_tokens: Int = 1000,
    val temperature: Double = 0.7
)

@Singleton
class GroqClient @Inject constructor() {
    
    suspend fun generateAdvice(
        apiKey: String,
        messages: List<GroqMessage>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = GroqRequest(messages = messages)
            val jsonRequest = buildJsonRequest(request)
            
            val url = URL("https://api.groq.com/openai/v1/chat/completions")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            connection.outputStream.use { outputStream ->
                outputStream.write(jsonRequest.toByteArray())
            }
            
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                val content = parseResponse(response)
                Result.success(content)
            } else {
                Result.failure(Exception("Groq API hatası: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun generateSpendingAdvice(
        categoryBreakdown: Map<String, Long>,
        totalExpense: Long,
        apiKey: String
    ): Result<String> {
        val prompt = buildString {
            appendLine("Harcama analizim:")
            appendLine("Toplam harcama: ${formatCurrency(totalExpense)}")
            appendLine()
            appendLine("Kategori bazında harcamalar:")
            categoryBreakdown.forEach { (category, amount) ->
                val percentage = if (totalExpense > 0) (amount.toFloat() / totalExpense.toFloat() * 100) else 0f
                appendLine("- $category: ${formatCurrency(amount)} (%${"%.1f".format(percentage)})")
            }
            appendLine()
            appendLine("Bu harcama alışkanlıklarım hakkında önerilerin neler?")
        }
        
        val messages = listOf(
            GroqMessage(
                role = "system",
                content = "Sen bir kişisel finans uzmanısın. Türkçe olarak kısa, net ve uygulanabilir finansal öneriler ver."
            ),
            GroqMessage(
                role = "user",
                content = prompt
            )
        )
        
        return generateAdvice(apiKey, messages)
    }
    
    suspend fun generateBudgetAdvice(
        income: Long,
        expenses: Long,
        savings: Long,
        apiKey: String
    ): Result<String> {
        val prompt = buildString {
            appendLine("Finansal durumum:")
            appendLine("Gelir: ${formatCurrency(income)}")
            appendLine("Gider: ${formatCurrency(expenses)}")
            appendLine("Tasarruf: ${formatCurrency(savings)}")
            appendLine()
            appendLine("Bu durumda nasıl daha iyi bütçe yönetimi yapabilirim?")
        }
        
        val messages = listOf(
            GroqMessage(
                role = "system",
                content = "Sen bir kişisel finans uzmanısın. Türkçe olarak kısa, net ve uygulanabilir finansal öneriler ver."
            ),
            GroqMessage(
                role = "user",
                content = prompt
            )
        )
        
        return generateAdvice(apiKey, messages)
    }
    
    private fun buildJsonRequest(request: GroqRequest): String {
        val json = JSONObject()
        json.put("model", request.model)
        json.put("max_tokens", request.max_tokens)
        json.put("temperature", request.temperature)
        
        val messagesArray = JSONArray()
        request.messages.forEach { message ->
            val messageObj = JSONObject()
            messageObj.put("role", message.role)
            messageObj.put("content", message.content)
            messagesArray.put(messageObj)
        }
        json.put("messages", messagesArray)
        
        return json.toString()
    }
    
    private fun parseResponse(response: String): String {
        return try {
            val json = JSONObject(response)
            val choices = json.getJSONArray("choices")
            if (choices.length() > 0) {
                val firstChoice = choices.getJSONObject(0)
                val message = firstChoice.getJSONObject("message")
                message.getString("content")
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
    
    private fun formatCurrency(amount: Long): String {
        return "₺${"%.2f".format(amount / 100.0)}"
    }
}