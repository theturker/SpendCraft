package com.alperen.spendcraft.shared.domain.repository

import com.alperen.spendcraft.shared.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    suspend fun getAllCategories(): List<Category>
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(categoryId: Long)
    suspend fun getCategoryById(categoryId: Long): Category?
}

