package com.alperen.spendcraft.core.ai

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIKeyManager @Inject constructor(
    private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "ai_keys",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveGroqApiKey(apiKey: String) {
        sharedPreferences.edit()
            .putString("groq_api_key", apiKey)
            .apply()
    }
    
    fun getGroqApiKey(): String? {
        return sharedPreferences.getString("groq_api_key", null)
    }
    
    fun hasGroqApiKey(): Boolean {
        return getGroqApiKey() != null
    }
    
    fun clearGroqApiKey() {
        sharedPreferences.edit()
            .remove("groq_api_key")
            .apply()
    }
}
