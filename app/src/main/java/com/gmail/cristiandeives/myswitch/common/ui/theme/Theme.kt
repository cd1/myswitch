package com.gmail.cristiandeives.myswitch.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MySwitchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = mySwitchColorScheme(darkTheme, dynamicColor),
        shapes = MySwitchShapes,
        typography = MySwitchTypography,
        content = content,
    )
}
