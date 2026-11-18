package com.sarayasecas.imc_saray.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/**
 * Títulos con letra serif (recuerda a la letra Mincho japonesa)
 * y textos normales con la letra del sistema.
 */
private val base = Typography()
private val Titulos = FontFamily.Serif

val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Titulos, fontWeight = FontWeight.Bold),
    displayMedium = base.displayMedium.copy(fontFamily = Titulos),
    headlineLarge = base.headlineLarge.copy(fontFamily = Titulos),
    headlineMedium = base.headlineMedium.copy(fontFamily = Titulos, fontWeight = FontWeight.SemiBold),
    headlineSmall = base.headlineSmall.copy(fontFamily = Titulos, fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontFamily = Titulos, fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontFamily = Titulos, fontWeight = FontWeight.SemiBold),
    titleSmall = base.titleSmall.copy(fontFamily = Titulos, fontWeight = FontWeight.SemiBold),
)
