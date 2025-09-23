package com.alperen.spendcraft

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*

object LocaleHelper {
    
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    
    fun setLocale(context: Context, language: String): Context {
        persist(context, language)
        return updateResources(context, language)
    }
    
    private fun persist(context: Context, language: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(SELECTED_LANGUAGE, language).apply()
    }
    
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            return context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            return context
        }
    }
    
    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_LANGUAGE, "tr") ?: "tr"
    }
}
