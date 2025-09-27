package com.alperen.spendcraft.data.repository

import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.data.db.dao.CategoryDao
import com.alperen.spendcraft.data.db.entities.CategoryEntity
import com.alperen.spendcraft.data.db.mappers.toCategory
import com.alperen.spendcraft.data.db.mappers.toCategoryEntity
import com.alperen.spendcraft.domain.repo.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun observeCategories(): Flow<List<Category>> {
        return categoryDao.observeCategories().map { entities ->
            entities.map { it.toCategory() }
        }
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories().map { it.toCategory() }
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toCategoryEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(
            id = category.id!!,
            name = category.name,
            color = category.color,
            icon = category.icon ?: ""
        )
    }

    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.deleteCategory(categoryId)
    }

    override suspend fun getCategoryById(categoryId: Long): Category? {
        return categoryDao.getCategoryById(categoryId)?.toCategory()
    }
}
