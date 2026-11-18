package com.sarayasecas.imc_saray.modelo

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.sarayasecas.imc_saray.R

enum class Sexo(val etiqueta: String) {
    HOMBRE("Hombre"),
    MUJER("Mujer"),
}

/**
 * Un rango de IMC para personas adultas (clasificación de la OMS).
 * [minimo] incluido y [maximo] excluido.
 */
data class RangoImc(
    val nombre: String,
    val minimo: Double,
    val maximo: Double,
    val color: Color,
    val comentario: String,
    @param:DrawableRes val ilustracionHombre: Int,
    @param:DrawableRes val ilustracionMujer: Int,
) {
    fun contiene(imc: Double): Boolean = imc >= minimo && imc < maximo

    fun ilustracion(sexo: Sexo): Int = when (sexo) {
        Sexo.HOMBRE -> ilustracionHombre
        Sexo.MUJER -> ilustracionMujer
    }

    /** Texto para la tabla de rangos con dos decimales, p. ej. "18,50 – 24,99". */
    val textoIntervalo: String
        get() = when {
            minimo == 0.0 -> "< ${formatear(maximo, 2)}"
            maximo == Double.MAX_VALUE -> "≥ ${formatear(minimo, 2)}"
            else -> "${formatear(minimo, 2)} – ${formatear(maximo - 0.01, 2)}"
        }
}

/**
 * Lista inmutable con la clasificación internacional del IMC en adultos (OMS),
 * ordenada de menor a mayor. Los colores son tradicionales japoneses:
 * azules índigo (delgadez), verde matcha (normal), caqui (preobesidad) y rojos (obesidad).
 */
val RANGOS_IMC: List<RangoImc> = listOf(
    RangoImc(
        "Delgadez severa", 0.0, 16.0, Color(0xFF1B3A6B),
        "Estás en los huesos y esto ya preocupa: come bien y pide cita con tu médico.",
        R.drawable.hombre_delgadez_severa, R.drawable.mujer_delgadez_severa,
    ),
    RangoImc(
        "Delgadez moderada", 16.0, 17.0, Color(0xFF2E5C8A),
        "Un día de viento sales volando: ponte un buen plato de lentejas.",
        R.drawable.hombre_delgadez_moderada, R.drawable.mujer_delgadez_moderada,
    ),
    RangoImc(
        "Delgadez leve", 17.0, 18.5, Color(0xFF5B8DB8),
        "Hazme un favor y cómete otro bocadillo más.",
        R.drawable.hombre_delgadez_leve, R.drawable.mujer_delgadez_leve,
    ),
    RangoImc(
        "Peso normal", 18.5, 25.0, Color(0xFF6A8D3A),
        "Estamos en línea, pero no te emociones y contrólate con las cervezas.",
        R.drawable.hombre_normal, R.drawable.mujer_normal,
    ),
    RangoImc(
        "Preobesidad", 25.0, 30.0, Color(0xFFE08A1E),
        "Se acercan las Navidades: tienes prohibidos los polvorones, por bicho bola.",
        R.drawable.hombre_sobrepeso, R.drawable.mujer_sobrepeso,
    ),
    RangoImc(
        "Obesidad tipo I", 30.0, 35.0, Color(0xFFD9472B),
        "Haz un favor al mundo y a tu salud y deja de comer Bollycaos.",
        R.drawable.hombre_obesidad_1, R.drawable.mujer_obesidad_1,
    ),
    RangoImc(
        "Obesidad tipo II", 35.0, 40.0, Color(0xFFB22222),
        "El sofá ya tiene tu forma: levántate y sal a andar antes de que te adopte.",
        R.drawable.hombre_obesidad_2, R.drawable.mujer_obesidad_2,
    ),
    RangoImc(
        "Obesidad tipo III", 40.0, Double.MAX_VALUE, Color(0xFF7B1E2B),
        "Esto ya no es broma: pide cita con tu médico, que tu salud va primero.",
        R.drawable.hombre_obesidad_3, R.drawable.mujer_obesidad_3,
    ),
)

/** Lista inmutable con las opciones de sexo que se muestran en el formulario. */
val OPCIONES_SEXO: List<Sexo> = Sexo.entries.toList()

data class Persona(
    val nombre: String,
    val edad: Int,
    val sexo: Sexo,
    val pesoKg: Double,
    val estaturaCm: Double,
) {
    /** IMC = peso (kg) / estatura (m)² */
    val imc: Double
        get() {
            val metros = estaturaCm / 100.0
            return pesoKg / (metros * metros)
        }

    /**
     * Se busca con el IMC redondeado a dos decimales, el mismo que se ve en pantalla.
     * Así un 34,996 (que se muestra como "35,00") cae en Obesidad tipo II y no en tipo I.
     */
    val imcRedondeado: Double
        get() = Math.round(imc * 100) / 100.0

    val rango: RangoImc
        get() = RANGOS_IMC.first { it.contiene(imcRedondeado) }
}

/** Valores que se usan como extremos de la barra para los rangos abiertos (< 16 y ≥ 40). */
const val IMC_MINIMO_BARRA = 14.0
const val IMC_MAXIMO_BARRA = 45.0

/**
 * Posición (de 0 a 1) del IMC en la barra de colores. Cada rango ocupa el mismo
 * ancho y dentro de su tramo el marcador avanza según el valor del IMC.
 */
fun posicionEnBarra(imc: Double): Float {
    val indice = RANGOS_IMC.indexOfFirst { it.contiene(imc) }
    val rango = RANGOS_IMC[indice]
    val desde = if (rango.minimo == 0.0) IMC_MINIMO_BARRA else rango.minimo
    val hasta = if (rango.maximo == Double.MAX_VALUE) IMC_MAXIMO_BARRA else rango.maximo
    val dentro = ((imc - desde) / (hasta - desde)).coerceIn(0.0, 1.0)
    return ((indice + dentro) / RANGOS_IMC.size).toFloat()
}

/** Formatea con coma decimal: formatear(22.456) -> "22,5"; formatear(22.456, 2) -> "22,46". */
fun formatear(valor: Double, decimales: Int = 1): String =
    String.format(java.util.Locale.forLanguageTag("es-ES"), "%.${decimales}f", valor)
