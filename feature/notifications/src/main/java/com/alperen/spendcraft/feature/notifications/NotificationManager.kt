package com.alperen.spendcraft.feature.notifications

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS NotificationManager.swift'in Android karşılığı
 * Singleton pattern ile bildirim template'lerini yönetir
 */
class NotificationManager private constructor(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "notification_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    // State flows for reactive UI
    private val _templates = MutableStateFlow<List<NotificationTemplate>>(emptyList())
    val templates: StateFlow<List<NotificationTemplate>> = _templates.asStateFlow()
    
    private val _customNotifications = MutableStateFlow<List<CustomNotification>>(emptyList())
    val customNotifications: StateFlow<List<CustomNotification>> = _customNotifications.asStateFlow()
    
    private val _isAuthorized = MutableStateFlow(false)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()
    
    companion object {
        @Volatile
        private var instance: NotificationManager? = null
        
        fun getInstance(context: Context): NotificationManager {
            return instance ?: synchronized(this) {
                instance ?: NotificationManager(context.applicationContext).also { 
                    instance = it 
                }
            }
        }
        
        fun shared(context: Context) = getInstance(context)
    }
    
    init {
        loadTemplates()
        loadCustomNotifications()
        checkNotificationPermission()
    }
    
    // Load templates from SharedPreferences
    private fun loadTemplates() {
        val savedJson = prefs.getString("templates", null)
        _templates.value = if (savedJson != null) {
            try {
                val type = object : TypeToken<List<NotificationTemplate>>() {}.type
                gson.fromJson(savedJson, type)
            } catch (e: Exception) {
                NotificationTemplate.getDefaultTemplates()
            }
        } else {
            NotificationTemplate.getDefaultTemplates()
        }
    }
    
    // Save templates to SharedPreferences
    private fun saveTemplates() {
        val json = gson.toJson(_templates.value)
        prefs.edit().putString("templates", json).apply()
    }
    
    // Load custom notifications from SharedPreferences
    private fun loadCustomNotifications() {
        val savedJson = prefs.getString("custom_notifications", null)
        _customNotifications.value = if (savedJson != null) {
            try {
                val type = object : TypeToken<List<CustomNotification>>() {}.type
                gson.fromJson(savedJson, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    // Save custom notifications to SharedPreferences
    private fun saveCustomNotifications() {
        val json = gson.toJson(_customNotifications.value)
        prefs.edit().putString("custom_notifications", json).apply()
    }
    
    // Check notification permission
    private fun checkNotificationPermission() {
        // TODO: Implement actual permission check
        // For now, assume authorized
        _isAuthorized.value = true
    }
    
    // Update template
    fun updateTemplate(template: NotificationTemplate) {
        _templates.value = _templates.value.map { 
            if (it.id == template.id) template else it 
        }
        saveTemplates()
    }
    
    // Add custom notification
    fun addCustomNotification(notification: CustomNotification) {
        _customNotifications.value = _customNotifications.value + notification
        saveCustomNotifications()
    }
    
    // Update custom notification
    fun updateCustomNotification(notification: CustomNotification) {
        _customNotifications.value = _customNotifications.value.map { 
            if (it.id == notification.id) notification else it 
        }
        saveCustomNotifications()
    }
    
    // Delete custom notification
    fun deleteCustomNotification(notification: CustomNotification) {
        _customNotifications.value = _customNotifications.value.filter { 
            it.id != notification.id 
        }
        saveCustomNotifications()
    }
    
    // Schedule notification (to be implemented with WorkManager)
    fun scheduleNotification(template: NotificationTemplate) {
        // TODO: Implement with WorkManager
    }
    
    fun scheduleNotification(notification: CustomNotification) {
        // TODO: Implement with WorkManager
    }
}


