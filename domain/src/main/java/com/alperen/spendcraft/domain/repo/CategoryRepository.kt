package com.alperen.spendcraft.domain.repo

import com.alperen.spendcraft.core.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    suspend fun getAllCategories(): List<Category>
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(categoryId: Long)
    suspend fun getCategoryById(categoryId: Long): Category?
}
