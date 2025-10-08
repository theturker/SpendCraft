package com.alperen.spendcraft.shared.platform

import platform.Foundation.NSUserDefaults

class PreferencesImpl : Preferences {
    
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    override fun getString(key: String, defaultValue: String): String {
        return userDefaults.stringForKey(key) ?: defaultValue
    }
    
    override fun getInt(key: String, defaultValue: Int): Int {
        return if (userDefaults.objectForKey(key) != null) {
            userDefaults.integerForKey(key).toInt()
        } else {
            defaultValue
        }
    }
    
    override fun getLong(key: String, defaultValue: Long): Long {
        return if (userDefaults.objectForKey(key) != null) {
            userDefaults.integerForKey(key)
        } else {
            defaultValue
        }
    }
    
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (userDefaults.objectForKey(key) != null) {
            userDefaults.boolForKey(key)
        } else {
            defaultValue
        }
    }
    
    override fun getFloat(key: String, defaultValue: Float): Float {
        return if (userDefaults.objectForKey(key) != null) {
            userDefaults.floatForKey(key)
        } else {
            defaultValue
        }
    }
    
    override fun putString(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
        userDefaults.synchronize()
    }
    
    override fun putInt(key: String, value: Int) {
        userDefaults.setInteger(value.toLong(), forKey = key)
        userDefaults.synchronize()
    }
    
    override fun putLong(key: String, value: Long) {
        userDefaults.setInteger(value, forKey = key)
        userDefaults.synchronize()
    }
    
    override fun putBoolean(key: String, value: Boolean) {
        userDefaults.setBool(value, forKey = key)
        userDefaults.synchronize()
    }
    
    override fun putFloat(key: String, value: Float) {
        userDefaults.setFloat(value, forKey = key)
        userDefaults.synchronize()
    }
    
    override fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
        userDefaults.synchronize()
    }
    
    override fun clear() {
        val domain = userDefaults.dictionaryRepresentation().keys
        domain.forEach { key ->
            if (key is String) {
                userDefaults.removeObjectForKey(key)
            }
        }
        userDefaults.synchronize()
    }
    
    override fun contains(key: String): Boolean {
        return userDefaults.objectForKey(key) != null
    }
}

