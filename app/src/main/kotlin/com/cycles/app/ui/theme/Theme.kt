package com.cycles.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = CyclesPrimary,
    onPrimary = CyclesOnPrimary,
    primaryContainer = CyclesPrimaryContainer,
    onPrimaryContainer = CyclesOnPrimaryContainer,
    secondary = CyclesSecondary,
    onSecondary = CyclesOnSecondary,
    secondaryContainer = CyclesSecondaryContainer,
    onSecondaryContainer = CyclesOnSecondaryContainer,
    background = CyclesBackground,
    surface = CyclesSurface,
    onBackground = CyclesOnBackground,
    onSurface = CyclesOnSurface,
    error = CyclesError,
    onError = CyclesOnError,
)

private val DarkColorScheme = darkColorScheme(
    primary = CyclesPrimaryDark,
    onPrimary = CyclesOnPrimaryDark,
    primaryContainer = CyclesPrimaryContainerDark,
    onPrimaryContainer = CyclesOnPrimaryContainerDark,
    secondary = CyclesSecondaryDark,
    onSecondary = CyclesOnSecondaryDark,
    secondaryContainer = CyclesSecondaryContainerDark,
    onSecondaryContainer = CyclesOnSecondaryContainerDark,
    background = CyclesBackgroundDark,
    surface = CyclesSurfaceDark,
    onBackground = CyclesOnBackgroundDark,
    onSurface = CyclesOnSurfaceDark,
    error = CyclesErrorDark,
    onError = CyclesOnErrorDark,
)

@Composable
fun CyclesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CyclesTypography,
        content = content,
    )
}
