package com.alperen.spendcraft.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alperen.spendcraft.data.db.dao.AccountDao
import com.alperen.spendcraft.data.db.dao.CategoryDao
import com.alperen.spendcraft.data.db.dao.TxDao
import com.alperen.spendcraft.data.db.entities.AccountEntity
import com.alperen.spendcraft.data.db.entities.CategoryEntity
import com.alperen.spendcraft.data.db.entities.TransactionEntity

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class, AccountEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {
    abstract fun txDao(): TxDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
}




