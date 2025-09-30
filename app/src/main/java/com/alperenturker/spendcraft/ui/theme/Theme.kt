package com.alperenturker.spendcraft.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.alperen.spendcraft.core.designsystem.theme.SpendCraftTheme as CoreSpendCraftTheme

@Composable
fun SpendCraftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    CoreSpendCraftTheme(
        darkTheme = darkTheme,
        reduceMotion = reduceMotion,
        content = content
    )
}
