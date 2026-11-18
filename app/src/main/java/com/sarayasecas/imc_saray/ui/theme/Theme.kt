package com.sarayasecas.imc_saray.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Estilo japonés: papel washi, tinta sumi, bermellón, índigo y dorado. */
private val LightColorScheme = lightColorScheme(
    primary = Shu,
    onPrimary = Color.White,
    secondary = Ai,
    onSecondary = Color.White,
    tertiary = Kin,
    onTertiary = Sumi,
    background = Washi,
    onBackground = Sumi,
    surface = Color(0xFFFFFBF2),
    onSurface = Sumi,
    surfaceVariant = WashiOscuro,
    onSurfaceVariant = Color(0xFF5C5348),
    surfaceContainer = Color(0xFFFFFBF2),
    surfaceContainerHighest = WashiOscuro,
    outline = Color(0xFFB8A98F),
    error = Color(0xFF9E1B1B),
)

private val DarkColorScheme = darkColorScheme(
    primary = ShuClaro,
    onPrimary = Noche,
    secondary = AiClaro,
    onSecondary = Noche,
    tertiary = KinClaro,
    onTertiary = Noche,
    background = Noche,
    onBackground = Washi,
    surface = NocheSuperficie,
    onSurface = Washi,
    surfaceVariant = Color(0xFF3A342D),
    onSurfaceVariant = Color(0xFFD8CCB8),
    surfaceContainer = NocheSuperficie,
    surfaceContainerHighest = Color(0xFF3A342D),
    outline = Color(0xFF7A6E5D),
)

/** Esquinas poco redondeadas, más sobrias. */
private val FormasJaponesas = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(12.dp),
)

@Composable
fun ImcSarayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Sin color dinámico: siempre se usan los colores japoneses
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = FormasJaponesas,
        content = content,
    )
}
