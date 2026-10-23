package com.linguapulse.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PulseGreen,
    secondary = PulseBlue,
    tertiary = PulseGold,
    background = PulseText,
    surface = PulseText
)

private val LightColorScheme = lightColorScheme(
    primary = PulseGreen,
    secondary = PulseBlue,
    tertiary = PulseGold,
    background = PulseBackground,
    surface = PulseSurface
)

@Composable
fun LinguaPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
