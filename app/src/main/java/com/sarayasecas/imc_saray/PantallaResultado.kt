package com.sarayasecas.imc_saray

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarayasecas.imc_saray.modelo.Persona
import com.sarayasecas.imc_saray.modelo.RANGOS_IMC
import com.sarayasecas.imc_saray.modelo.RangoImc
import com.sarayasecas.imc_saray.modelo.Sexo
import com.sarayasecas.imc_saray.modelo.formatear
import com.sarayasecas.imc_saray.modelo.posicionEnBarra
import com.sarayasecas.imc_saray.ui.theme.ImcSarayTheme

@Composable
fun PantallaResultado(persona: Persona, onVolver: () -> Unit, modifier: Modifier = Modifier) {
    val rango = persona.rango
    val borde = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f))

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = borde,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(persona.nombre, style = MaterialTheme.typography.headlineMedium)
                Text(
                    "${persona.sexo.etiqueta} · ${persona.edad} años · ${formatear(persona.pesoKg)} kg · ${formatear(persona.estaturaCm)} cm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                Text("Su IMC es", style = MaterialTheme.typography.titleMedium)
                // Valor numérico
                Text(
                    formatear(persona.imc, 2),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                )
                // Valor escrito
                Text(
                    rango.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    color = rango.color,
                    fontWeight = FontWeight.SemiBold,
                )
                // Barra de colores con el nivel de la persona
                BarraNivel(imc = persona.imcRedondeado, modifier = Modifier.padding(top = 12.dp))
                // Comentario personal según el rango
                Text(
                    "“${rango.comentario}”",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .background(rango.color.copy(alpha = 0.12f), MaterialTheme.shapes.small)
                        .border(1.dp, rango.color.copy(alpha = 0.5f), MaterialTheme.shapes.small)
                        .padding(12.dp),
                )
                Spacer(Modifier.height(12.dp))
                Image(
                    painter = painterResource(rango.ilustracion(persona.sexo)),
                    contentDescription = "${if (persona.sexo == Sexo.HOMBRE) "Samurái" else "Geisha"} con ${rango.nombre.replaceFirstChar { it.lowercase() }}",
                    modifier = Modifier.height(220.dp),
                )
            }
        }

        TablaRangos(rangoActual = rango, borde = borde)

        Button(onClick = onVolver, shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

/**
 * Barra con un tramo de color por cada rango de la OMS (todos del mismo ancho)
 * y un marcador triangular con el IMC encima, en la posición de la persona.
 */
@Composable
private fun BarraNivel(imc: Double, modifier: Modifier = Modifier) {
    val posicion = posicionEnBarra(imc)
    val colorMarcador = MaterialTheme.colorScheme.onSurface
    val anchoEtiqueta = 56.dp
    val anchoTriangulo = 14.dp

    BoxWithConstraints(modifier.fillMaxWidth()) {
        val ancho = maxWidth
        val x = ancho * posicion

        Column {
            // Valor del IMC encima del marcador (sin salirse por los lados)
            Text(
                formatear(imc, 2),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(anchoEtiqueta)
                    .offset(x = (x - anchoEtiqueta / 2).coerceIn(0.dp, ancho - anchoEtiqueta)),
            )
            // Triángulo que apunta a la barra
            Canvas(
                Modifier
                    .offset(x = (x - anchoTriangulo / 2).coerceIn(0.dp, ancho - anchoTriangulo))
                    .size(anchoTriangulo, 9.dp),
            ) {
                val punta = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2, size.height)
                    close()
                }
                drawPath(punta, colorMarcador)
            }
            // Un tramo de color por rango
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(MaterialTheme.shapes.extraSmall),
            ) {
                RANGOS_IMC.forEach { rango ->
                    Box(Modifier.weight(1f).fillMaxHeight().background(rango.color))
                }
            }
            // Límites entre tramos: 16, 17, 18,5, 25, 30, 35, 40
            Box(Modifier.fillMaxWidth().height(16.dp)) {
                val anchoLimite = 32.dp
                RANGOS_IMC.drop(1).forEachIndexed { i, rango ->
                    Text(
                        formatear(rango.minimo).removeSuffix(",0"),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .width(anchoLimite)
                            .offset(x = ancho * (i + 1) / RANGOS_IMC.size - anchoLimite / 2),
                    )
                }
            }
        }
    }
}

@Composable
private fun TablaRangos(rangoActual: RangoImc, borde: BorderStroke) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = borde,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Rangos de IMC (OMS, adultos)", style = MaterialTheme.typography.titleMedium)
            RANGOS_IMC.forEach { rango ->
                val esActual = rango == rangoActual
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (esActual) rango.color.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                            MaterialTheme.shapes.small,
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    // Cuadradito de color, como un sello japonés
                    Spacer(Modifier.size(12.dp).background(rango.color, MaterialTheme.shapes.extraSmall))
                    Text(
                        rango.nombre,
                        modifier = Modifier.weight(1f).padding(start = 12.dp),
                        fontWeight = if (esActual) FontWeight.Bold else FontWeight.Normal,
                    )
                    Text(
                        rango.textoIntervalo,
                        fontWeight = if (esActual) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PantallaResultadoPreview() {
    ImcSarayTheme {
        PantallaResultado(Persona("Lucía", 34, Sexo.MUJER, 62.0, 165.0), onVolver = {})
    }
}
