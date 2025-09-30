package com.alperen.spendcraft.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class SpendCraftShapeTokens(
    val roundedXs: Shape,
    val roundedSm: Shape,
    val roundedMd: Shape,
    val roundedLg: Shape,
    val roundedXl: Shape,
    val pill: Shape,
    val circular: Shape
)

@Stable
class SpendCraftShapes internal constructor(
    internal val material: Shapes,
    val tokens: SpendCraftShapeTokens
) {
    val extraSmall: Shape get() = material.extraSmall
    val small: Shape get() = material.small
    val medium: Shape get() = material.medium
    val large: Shape get() = material.large
    val extraLarge: Shape get() = material.extraLarge
}

private val MaterialShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

private val ShapeTokens = SpendCraftShapeTokens(
    roundedXs = RoundedCornerShape(6.dp),
    roundedSm = RoundedCornerShape(10.dp),
    roundedMd = RoundedCornerShape(14.dp),
    roundedLg = RoundedCornerShape(20.dp),
    roundedXl = RoundedCornerShape(28.dp),
    pill = RoundedCornerShape(50),
    circular = CircleShape
)

internal fun spendCraftShapes(): SpendCraftShapes =
    SpendCraftShapes(
        material = MaterialShapes,
        tokens = ShapeTokens
    )
