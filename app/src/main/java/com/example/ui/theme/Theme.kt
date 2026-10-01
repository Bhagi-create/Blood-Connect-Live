package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFEF5350),
    onPrimary = Color.White,
    primaryContainer = BloodRedDark,
    onPrimaryContainer = Color(0xFFFFCDD2),
    secondary = Color(0xFFFF8A80),
    onSecondary = Color.Black,
    surface = DarkSurface,
    onSurface = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCCCCCC),
    error = Color(0xFFFF5252),
    onError = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BloodRedPrimary,
    onPrimary = Color.White,
    primaryContainer = BloodRedLight,
    onPrimaryContainer = BloodRedDark,
    secondary = BloodRedAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEBEE),
    onSecondaryContainer = BloodRedDark,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderColor,
    error = Color(0xFFD32F2F),
    onError = Color.White
  )

@Composable
fun BloodConnectTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent branding colors by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  BloodConnectTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
