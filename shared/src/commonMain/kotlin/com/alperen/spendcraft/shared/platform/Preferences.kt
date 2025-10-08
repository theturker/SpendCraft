package com.alperen.spendcraft.shared.platform

/**
 * Platform-agnostic key-value storage
 */
interface Preferences {
    fun getString(key: String, defaultValue: String = ""): String
    fun getInt(key: String, defaultValue: Int = 0): Int
    fun getLong(key: String, defaultValue: Long = 0L): Long
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun getFloat(key: String, defaultValue: Float = 0f): Float
    
    fun putString(key: String, value: String)
    fun putInt(key: String, value: Int)
    fun putLong(key: String, value: Long)
    fun putBoolean(key: String, value: Boolean)
    fun putFloat(key: String, value: Float)
    
    fun remove(key: String)
    fun clear()
    fun contains(key: String): Boolean
}

// PreferencesImpl her platformda kendi module'ünde tanımlanacak

