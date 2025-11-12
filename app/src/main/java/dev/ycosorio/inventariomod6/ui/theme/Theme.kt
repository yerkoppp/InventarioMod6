package dev.ycosorio.inventariomod6.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = InventarisPurple80,
    secondary = InventarisGreen80,
    tertiary = InventarisComplement80,
    // Puedes definir otros colores como 'background' o 'surface' aquí
    // si quieres un control total, o dejar que M3 los genere.
)

private val LightColorScheme = lightColorScheme(
    primary = InventarisPurple40,
    secondary = InventarisGreen40,
    tertiary = InventarisComplement40

    /*
    Otros colores por defecto para sobreescribir (opcional):
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun InventarioMod6Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // El color dinámico está disponible en Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Usa colores dinámicos si está disponible (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Usa la paleta oscura personalizada
        darkTheme -> DarkColorScheme
        // Usa la paleta clara personalizada
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Tu archivo Type.kt
        content = content
    )
}