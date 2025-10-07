package com.alperen.spendcraft.shared.domain.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsEvent(
    val id: Long? = null,
    val eventName: String,
    val eventData: Map<String, String> = emptyMap(),
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val sessionId: String,
    val userId: String = "default_user"
)

enum class EventType(val eventName: String) {
    // App Events
    APP_OPENED("app_opened"),
    APP_BACKGROUNDED("app_backgrounded"),
    APP_FOREGROUNDED("app_foregrounded"),
    
    // Transaction Events
    TRANSACTION_ADDED("transaction_added"),
    TRANSACTION_EDITED("transaction_edited"),
    TRANSACTION_DELETED("transaction_deleted"),
    QUICK_ADD_USED("quick_add_used"),
    
    // Category Events
    CATEGORY_ADDED("category_added"),
    CATEGORY_DELETED("category_deleted"),
    
    // Account Events
    ACCOUNT_CREATED("account_created"),
    ACCOUNT_EDITED("account_edited"),
    ACCOUNT_DELETED("account_deleted"),
    
    // Budget Events
    BUDGET_CREATED("budget_created"),
    BUDGET_UPDATED("budget_updated"),
    BUDGET_DELETED("budget_deleted"),
    BUDGET_BREACH("budget_breach"),
    
    // Streak Events
    STREAK_ACHIEVED("streak_achieved"),
    STREAK_BROKEN("streak_broken"),
    BADGE_EARNED("badge_earned"),
    
    // Onboarding Events
    ONBOARDING_STARTED("onboarding_started"),
    ONBOARDING_COMPLETED("onboarding_completed"),
    ONBOARDING_SKIPPED("onboarding_skipped"),
    
    // Import/Export Events
    CSV_IMPORTED("csv_imported"),
    CSV_EXPORTED("csv_exported"),
    
    // Widget Events
    WIDGET_ADDED("widget_added"),
    WIDGET_CLICKED("widget_clicked"),
    
    // Settings Events
    THEME_CHANGED("theme_changed"),
    LANGUAGE_CHANGED("language_changed"),
    CURRENCY_CHANGED("currency_changed"),
    
    // Error Events
    ERROR_OCCURRED("error_occurred"),
    CRASH_OCCURRED("crash_occurred"),
    
    // Premium Events
    PREMIUM_FEATURE_CLICKED("premium_feature_clicked"),
    PAYWALL_SHOWN("paywall_shown"),
    PURCHASE_INITIATED("purchase_initiated"),
    PURCHASE_COMPLETED("purchase_completed"),
    PURCHASE_FAILED("purchase_failed")
}

