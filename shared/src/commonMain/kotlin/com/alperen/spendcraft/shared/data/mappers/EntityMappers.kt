package com.alperen.spendcraft.shared.data.mappers

import com.alperen.spendcraft.shared.domain.model.Category
import com.alperen.spendcraft.shared.database.CategoryEntity

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        color = color,
        icon = icon
    )
}

