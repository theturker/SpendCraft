package com.alperen.spendcraft.shared.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.alperen.spendcraft.shared.database.SpendCraftDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = SpendCraftDatabase.Schema,
            context = context,
            name = "spendcraft.db"
        )
    }
}

