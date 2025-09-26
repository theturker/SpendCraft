package com.alperen.spendcraft.core.ai

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIKeyManager @Inject constructor(
    @ApplicationContext private val context: Context
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
    
    private val GROQ_API_KEY = "groq_api_key"
    
    fun getGroqApiKey(): String? {
        return "apikey"
    }
    
    fun setGroqApiKey(apiKey: String) {
        sharedPreferences.edit()
            .putString(GROQ_API_KEY, apiKey)
            .apply()
    }
    
    fun hasGroqApiKey(): Boolean {
        return getGroqApiKey() != null
    }
    
    fun clearApiKey() {
        sharedPreferences.edit()
            .remove(GROQ_API_KEY)
            .apply()
    }
}
