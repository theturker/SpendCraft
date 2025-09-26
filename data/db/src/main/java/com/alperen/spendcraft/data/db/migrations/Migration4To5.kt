package com.alperen.spendcraft.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_TO_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns to accounts table with proper defaults
        database.execSQL("ALTER TABLE accounts ADD COLUMN type TEXT NOT NULL DEFAULT 'CASH'")
        database.execSQL("ALTER TABLE accounts ADD COLUMN currency TEXT NOT NULL DEFAULT 'TRY'")
        database.execSQL("ALTER TABLE accounts ADD COLUMN archived INTEGER NOT NULL DEFAULT 0")
        
        // Update existing accounts to have proper values
        database.execSQL("UPDATE accounts SET type = 'CASH' WHERE type IS NULL OR type = ''")
        database.execSQL("UPDATE accounts SET currency = 'TRY' WHERE currency IS NULL OR currency = ''")
        database.execSQL("UPDATE accounts SET archived = 0 WHERE archived IS NULL")
        
        // Create indices for accounts table
        database.execSQL("CREATE INDEX IF NOT EXISTS index_accounts_isDefault ON accounts (isDefault)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_accounts_archived ON accounts (archived)")
        
        // Create recurring_rules table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS recurring_rules (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                templateTransactionId INTEGER NOT NULL,
                frequency TEXT NOT NULL,
                interval INTEGER NOT NULL DEFAULT 1,
                nextRunEpoch INTEGER NOT NULL,
                endEpoch INTEGER,
                isActive INTEGER NOT NULL DEFAULT 1
            )
        """)
        
        // Create indices for recurring_rules
        database.execSQL("CREATE INDEX IF NOT EXISTS index_recurring_rules_nextRunEpoch ON recurring_rules (nextRunEpoch)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_recurring_rules_templateTransactionId ON recurring_rules (templateTransactionId)")
        
        // Create sharing_members table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS sharing_members (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                householdId TEXT NOT NULL,
                userId TEXT NOT NULL,
                role TEXT NOT NULL,
                invitedAt INTEGER NOT NULL,
                joinedAt INTEGER
            )
        """)
        
        // Create indices for sharing_members
        database.execSQL("CREATE INDEX IF NOT EXISTS index_sharing_members_householdId ON sharing_members (householdId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_sharing_members_userId ON sharing_members (userId)")
        
        // Create ai_usage table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS ai_usage (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userId TEXT NOT NULL,
                lastUsedEpoch INTEGER NOT NULL DEFAULT 0,
                weeklyQuota INTEGER NOT NULL DEFAULT 1,
                usedThisWeek INTEGER NOT NULL DEFAULT 0
            )
        """)
        
        // Create index for ai_usage
        database.execSQL("CREATE INDEX IF NOT EXISTS index_ai_usage_userId ON ai_usage (userId)")
    }
}
