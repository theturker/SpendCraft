package com.alperen.spendcraft.shared.platform

import android.content.Context
import android.content.SharedPreferences

actual class PreferencesImpl(context: Context) : Preferences {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("SpendCraftPrefs", Context.MODE_PRIVATE)
    
    actual override fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    
    actual override fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }
    
    actual override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }
    
    actual override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
    
    actual override fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }
    
    actual override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
    
    actual override fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }
    
    actual override fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }
    
    actual override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
    
    actual override fun putFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }
    
    actual override fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
    
    actual override fun clear() {
        sharedPreferences.edit().clear().apply()
    }
    
    actual override fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}

