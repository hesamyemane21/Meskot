package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MeskotInk,
    onPrimary = Color.White,
    primaryContainer = MeskotPaper2,
    onPrimaryContainer = MeskotInk,
    secondary = MeskotGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF9EED9),
    onSecondaryContainer = MeskotGoldDeep,
    tertiary = MeskotCrimson,
    onTertiary = Color.White,
    background = MeskotPaper,
    onBackground = MeskotInk,
    surface = MeskotCard,
    onSurface = MeskotInk,
    surfaceVariant = MeskotPaper2,
    onSurfaceVariant = MeskotMuted,
    outline = MeskotLine
)

private val DarkColorScheme = darkColorScheme(
    primary = MeskotGold,
    onPrimary = MeskotInk,
    primaryContainer = Color(0xFF283D31),
    onPrimaryContainer = Color.White,
    secondary = MeskotGoldDeep,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF382914),
    onSecondaryContainer = Color(0xFFFFE0B2),
    tertiary = Color(0xFFE57373),
    onTertiary = Color.Black,
    background = Color(0xFF131D17),
    onBackground = Color(0xFFEEF2ED),
    surface = Color(0xFF1B2A22),
    onSurface = Color(0xFFEEF2ED),
    surfaceVariant = Color(0xFF24362C),
    onSurfaceVariant = Color(0xFFB5C0B7),
    outline = Color(0xFF3B4E42)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
