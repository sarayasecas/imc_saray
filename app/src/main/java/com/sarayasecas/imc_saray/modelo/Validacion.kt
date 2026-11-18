package com.sarayasecas.imc_saray.modelo

/*
 * Validación de los campos del formulario.
 * Cada función devuelve null si el valor es correcto, o el mensaje de error.
 */

const val EDAD_MINIMA = 18
const val EDAD_MAXIMA = 90
const val PESO_MINIMO = 20.0
const val PESO_MAXIMO = 300.0
const val ESTATURA_MINIMA = 100.0
const val ESTATURA_MAXIMA = 250.0

private val REGEX_NOMBRE = Regex("^[\\p{L} '-]+$")

/** Acepta "70,5" y "70.5". Devuelve null si no es un número. */
fun leerNumero(texto: String): Double? = texto.trim().replace(',', '.').toDoubleOrNull()

fun validarNombre(nombre: String): String? {
    val limpio = nombre.trim()
    return when {
        limpio.isEmpty() -> "Escribe el nombre"
        limpio.length < 2 -> "El nombre es demasiado corto"
        limpio.length > 40 -> "Máximo 40 caracteres"
        !REGEX_NOMBRE.matches(limpio) -> "Solo letras, espacios y guiones"
        else -> null
    }
}

/** La edad tiene que ser un número entero (sin decimales ni signo) entre 18 y 90. */
fun validarEdad(texto: String): String? {
    val limpio = texto.trim()
    if (limpio.isEmpty()) return "Escribe la edad"
    if (!limpio.all { it.isDigit() }) return "La edad debe ser un número entero, sin decimales"
    val edad = limpio.toIntOrNull() ?: return "Edad no válida"
    return if (edad < EDAD_MINIMA || edad > EDAD_MAXIMA) {
        "Debe estar entre $EDAD_MINIMA y $EDAD_MAXIMA años"
    } else null
}

fun validarSexo(sexo: Sexo?): String? = if (sexo == null) "Elige el sexo" else null

fun validarPeso(texto: String): String? {
    if (texto.isBlank()) return "Escribe el peso"
    val peso = leerNumero(texto) ?: return "El peso debe ser un número"
    return if (peso < PESO_MINIMO || peso > PESO_MAXIMO) {
        "Entre ${PESO_MINIMO.toInt()} y ${PESO_MAXIMO.toInt()} kg"
    } else null
}

fun validarEstatura(texto: String): String? {
    if (texto.isBlank()) return "Escribe la estatura"
    val estatura = leerNumero(texto) ?: return "La estatura debe ser un número"
    return if (estatura < ESTATURA_MINIMA || estatura > ESTATURA_MAXIMA) {
        "Entre ${ESTATURA_MINIMA.toInt()} y ${ESTATURA_MAXIMA.toInt()} cm"
    } else null
}
