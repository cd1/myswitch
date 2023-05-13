package com.gmail.cristiandeives.myswitch.common.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val sheikahBlue = Color(129, 207, 233)
val sheikahOrange = Color(239, 90, 20)
val sheikahBrown = Color(176, 136, 88)

private val lightColorScheme = lightColorScheme(
    primary = sheikahBlue,
    secondary = sheikahOrange,
    tertiary = sheikahBrown,
)

@Composable
fun mySwitchColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
): ColorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val context = LocalContext.current
    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
} else {
    lightColorScheme
}
