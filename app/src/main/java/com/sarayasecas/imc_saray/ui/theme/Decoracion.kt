package com.sarayasecas.imc_saray.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp

/**
 * Franja con el dibujo japonés de olas «seigaiha» (青海波):
 * filas de abanicos de círculos concéntricos, cada fila desplazada media ola.
 */
@Composable
fun BandaSeigaiha(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
    fondo: Color = MaterialTheme.colorScheme.background,
) {
    Canvas(modifier.fillMaxWidth().height(22.dp)) {
        val r = 12.dp.toPx()
        val trazo = Stroke(width = 1.2.dp.toPx())
        clipRect {
            var fila = 0
            var cy = 0f
            while (cy < size.height + r) {
                var cx = if (fila % 2 == 0) 0f else r
                while (cx < size.width + r) {
                    val centro = Offset(cx, cy)
                    drawCircle(fondo, r, centro)
                    drawCircle(color, r, centro, style = trazo)
                    drawCircle(color, r * 0.68f, centro, style = trazo)
                    drawCircle(color, r * 0.36f, centro, style = trazo)
                    cx += 2 * r
                }
                cy += r / 2
                fila++
            }
        }
    }
}
