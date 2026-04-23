package com.example.progressbar.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)

private val LightColorScheme = lightColorScheme(
    // Primary: Used for FilledButton, FloatingActionButton, etc.
    primary = Forest40,
    onPrimary = Color.White,
    primaryContainer = GreenLight80,
    onPrimaryContainer = Forest90,

    // Secondary: Used for OutlinedButton, chips, etc.
    secondary = GreenGrey90,
    onSecondary = Color.White,
    secondaryContainer = GreenLight80,
    onSecondaryContainer = Forest90,

    // Tertiary: Accent color for special actions
    tertiary = Orange40,
    onTertiary = Color.White,

    // Backgrounds & surfaces
    background = Color(0xFFFDFDFD),
    onBackground = GreenGrey90,
    surface = Color.White,
    onSurface = GreenGrey90,
    surfaceVariant = GreenLight80,
    onSurfaceVariant = Forest90,

    // Semantic colors
    error = Red40,
    onError = Color.White,
    errorContainer = Red40.copy(alpha = 0.1f),
    onErrorContainer = Red40,

    // Outline & borders
    outline = GreenGrey80,
    outlineVariant = YellowGrey40,

    // Other
    inverseSurface = Forest90,
    inverseOnSurface = Color.White,
    inversePrimary = Forest40,

    // Scrim & surfaces with alpha
    surfaceTint = Forest40,
    scrim = Color.Black.copy(alpha = 0.32f)
)

@Composable
fun ProgressBarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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
        typography = Typography,
        content = content
    )
}