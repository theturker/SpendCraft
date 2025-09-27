package com.alperen.spendcraft.data.db.mappers

import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.data.db.entities.CategoryEntity

fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        color = color,
        icon = icon
    )
}

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id ?: 0,
        name = name,
        color = color,
        icon = icon
    )
}
