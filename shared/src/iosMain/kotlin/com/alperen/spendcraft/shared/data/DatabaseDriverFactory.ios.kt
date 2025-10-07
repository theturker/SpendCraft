package com.alperen.spendcraft.shared.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.alperen.spendcraft.shared.database.SpendCraftDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = SpendCraftDatabase.Schema,
            name = "spendcraft.db"
        )
    }
}

