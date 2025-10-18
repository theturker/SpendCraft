package com.alperen.spendcraft.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 7 to 8
 * Adds isIncome field to categories table for type-specific filtering
 * iOS pattern: Categories are separated by income/expense type
 */
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add isIncome column to categories table with default value false (expense)
        db.execSQL("ALTER TABLE categories ADD COLUMN isIncome INTEGER NOT NULL DEFAULT 0")
    }
}

