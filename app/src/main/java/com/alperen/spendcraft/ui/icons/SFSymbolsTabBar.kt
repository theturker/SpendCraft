package com.alperen.spendcraft.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * iOS SF Symbols - Tab Bar Icons
 * 
 * iOS'taki tab bar icon'larının birebir Android vector karşılıkları
 * Size: 25x25dp (iOS .tabBar icon size)
 */
object SFSymbolsTabBar {
    
    /**
     * SF Symbol: house.fill
     * Ana Sayfa / Dashboard icon
     */
    val HouseFill: ImageVector
        get() = ImageVector.Builder(
            name = "HouseFill",
            defaultWidth = 25.dp,
            defaultHeight = 25.dp,
            viewportWidth = 25f,
            viewportHeight = 25f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12.5f, 3f)
                lineTo(2f, 11f)
                horizontalLineTo(5f)
                verticalLineTo(22f)
                horizontalLineTo(10f)
                verticalLineTo(16f)
                horizontalLineTo(15f)
                verticalLineTo(22f)
                horizontalLineTo(20f)
                verticalLineTo(11f)
                horizontalLineTo(23f)
                close()
            }
        }.build()
    
    /**
     * SF Symbol: list.bullet
     * İşlemler / Transactions icon
     */
    val ListBullet: ImageVector
        get() = ImageVector.Builder(
            name = "ListBullet",
            defaultWidth = 25.dp,
            defaultHeight = 25.dp,
            viewportWidth = 25f,
            viewportHeight = 25f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                // Bullet 1
                moveTo(4f, 6f)
                curveTo(4f, 5.17f, 4.67f, 4.5f, 5.5f, 4.5f)
                curveTo(6.33f, 4.5f, 7f, 5.17f, 7f, 6f)
                curveTo(7f, 6.83f, 6.33f, 7.5f, 5.5f, 7.5f)
                curveTo(4.67f, 7.5f, 4f, 6.83f, 4f, 6f)
                close()
                // Line 1
                moveTo(9f, 5f)
                horizontalLineTo(22f)
                verticalLineTo(7f)
                horizontalLineTo(9f)
                close()
                
                // Bullet 2
                moveTo(4f, 12.5f)
                curveTo(4f, 11.67f, 4.67f, 11f, 5.5f, 11f)
                curveTo(6.33f, 11f, 7f, 11.67f, 7f, 12.5f)
                curveTo(7f, 13.33f, 6.33f, 14f, 5.5f, 14f)
                curveTo(4.67f, 14f, 4f, 13.33f, 4f, 12.5f)
                close()
                // Line 2
                moveTo(9f, 11.5f)
                horizontalLineTo(22f)
                verticalLineTo(13.5f)
                horizontalLineTo(9f)
                close()
                
                // Bullet 3
                moveTo(4f, 19f)
                curveTo(4f, 18.17f, 4.67f, 17.5f, 5.5f, 17.5f)
                curveTo(6.33f, 17.5f, 7f, 18.17f, 7f, 19f)
                curveTo(7f, 19.83f, 6.33f, 20.5f, 5.5f, 20.5f)
                curveTo(4.67f, 20.5f, 4f, 19.83f, 4f, 19f)
                close()
                // Line 3
                moveTo(9f, 18f)
                horizontalLineTo(22f)
                verticalLineTo(20f)
                horizontalLineTo(9f)
                close()
            }
        }.build()
    
    /**
     * SF Symbol: chart.bar.fill
     * Raporlar / Reports icon
     */
    val ChartBarFill: ImageVector
        get() = ImageVector.Builder(
            name = "ChartBarFill",
            defaultWidth = 25.dp,
            defaultHeight = 25.dp,
            viewportWidth = 25f,
            viewportHeight = 25f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                // Bar 1 (shortest)
                moveTo(4f, 16f)
                horizontalLineTo(8f)
                verticalLineTo(21f)
                horizontalLineTo(4f)
                close()
                
                // Bar 2 (medium)
                moveTo(10f, 12f)
                horizontalLineTo(14f)
                verticalLineTo(21f)
                horizontalLineTo(10f)
                close()
                
                // Bar 3 (tallest)
                moveTo(16f, 7f)
                horizontalLineTo(20f)
                verticalLineTo(21f)
                horizontalLineTo(16f)
                close()
            }
        }.build()
    
    /**
     * SF Symbol: gearshape.fill
     * Ayarlar / Settings icon
     */
    val GearshapeFill: ImageVector
        get() = ImageVector.Builder(
            name = "GearshapeFill",
            defaultWidth = 25.dp,
            defaultHeight = 25.dp,
            viewportWidth = 25f,
            viewportHeight = 25f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                // Gear shape - simplified version
                moveTo(12.5f, 2f)
                lineTo(14.5f, 6f)
                lineTo(19f, 6.5f)
                lineTo(21f, 11f)
                lineTo(18f, 14.5f)
                lineTo(18.5f, 19f)
                lineTo(14f, 21f)
                lineTo(10f, 18f)
                lineTo(5.5f, 18.5f)
                lineTo(3f, 14f)
                lineTo(6f, 10.5f)
                lineTo(5.5f, 6f)
                lineTo(10f, 3f)
                close()
                
                // Center circle
                moveTo(12.5f, 16f)
                curveTo(14.43f, 16f, 16f, 14.43f, 16f, 12.5f)
                curveTo(16f, 10.57f, 14.43f, 9f, 12.5f, 9f)
                curveTo(10.57f, 9f, 9f, 10.57f, 9f, 12.5f)
                curveTo(9f, 14.43f, 10.57f, 16f, 12.5f, 16f)
                close()
            }
        }.build()
    
    /**
     * SF Symbol: folder.fill
     * Kategoriler / Categories icon
     */
    val FolderFill: ImageVector
        get() = ImageVector.Builder(
            name = "FolderFill",
            defaultWidth = 25.dp,
            defaultHeight = 25.dp,
            viewportWidth = 25f,
            viewportHeight = 25f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 6f)
                horizontalLineTo(12f)
                lineTo(10f, 4f)
                horizontalLineTo(4f)
                curveTo(2.9f, 4f, 2.01f, 4.9f, 2.01f, 6f)
                lineTo(2f, 18f)
                curveTo(2f, 19.1f, 2.9f, 20f, 4f, 20f)
                horizontalLineTo(20f)
                curveTo(21.1f, 20f, 22f, 19.1f, 22f, 18f)
                verticalLineTo(8f)
                curveTo(22f, 6.9f, 21.1f, 6f, 20f, 6f)
                close()
            }
        }.build()
}

