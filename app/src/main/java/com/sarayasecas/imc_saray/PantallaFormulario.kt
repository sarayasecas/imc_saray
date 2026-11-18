package com.sarayasecas.imc_saray

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarayasecas.imc_saray.modelo.EDAD_MAXIMA
import com.sarayasecas.imc_saray.modelo.EDAD_MINIMA
import com.sarayasecas.imc_saray.modelo.OPCIONES_SEXO
import com.sarayasecas.imc_saray.modelo.validarEdad
import com.sarayasecas.imc_saray.modelo.Sexo
import com.sarayasecas.imc_saray.modelo.validarEstatura
import com.sarayasecas.imc_saray.modelo.validarNombre
import com.sarayasecas.imc_saray.modelo.validarPeso
import com.sarayasecas.imc_saray.modelo.validarSexo
import com.sarayasecas.imc_saray.ui.theme.ImcSarayTheme

/**
 * Formulario de datos. El estado vive en App() para que no se pierda al volver
 * del resultado. Los errores se muestran a partir del primer intento de calcular.
 */
@Composable
fun PantallaFormulario(
    nombre: String,
    onNombreChange: (String) -> Unit,
    edad: String,
    onEdadChange: (String) -> Unit,
    sexo: Sexo?,
    onSexoChange: (Sexo) -> Unit,
    peso: String,
    onPesoChange: (String) -> Unit,
    estatura: String,
    onEstaturaChange: (String) -> Unit,
    mostrarErrores: Boolean,
    onCalcular: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorNombre = if (mostrarErrores) validarNombre(nombre) else null
    val errorEdad = if (mostrarErrores) validarEdad(edad) else null
    val errorSexo = if (mostrarErrores) validarSexo(sexo) else null
    val errorPeso = if (mostrarErrores) validarPeso(peso) else null
    val errorEstatura = if (mostrarErrores) validarEstatura(estatura) else null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Datos de la persona adulta", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = { Text("Nombre") },
            singleLine = true,
            isError = errorNombre != null,
            supportingText = errorNombre?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = edad,
            onValueChange = onEdadChange,
            label = { Text("Edad") },
            suffix = { Text("años") },
            singleLine = true,
            isError = errorEdad != null,
            supportingText = { Text(errorEdad ?: "Entre $EDAD_MINIMA y $EDAD_MAXIMA años") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )

        Text("Sexo", style = MaterialTheme.typography.titleSmall)
        Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OPCIONES_SEXO.forEach { opcion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectable(
                        selected = sexo == opcion,
                        onClick = { onSexoChange(opcion) },
                        role = Role.RadioButton,
                    ),
                ) {
                    RadioButton(selected = sexo == opcion, onClick = null)
                    Text(opcion.etiqueta, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
                }
            }
        }
        if (errorSexo != null) {
            Text(
                errorSexo,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp),
            )
        }

        OutlinedTextField(
            value = peso,
            onValueChange = onPesoChange,
            label = { Text("Peso") },
            suffix = { Text("kg") },
            singleLine = true,
            isError = errorPeso != null,
            supportingText = errorPeso?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = estatura,
            onValueChange = onEstaturaChange,
            label = { Text("Estatura") },
            suffix = { Text("cm") },
            singleLine = true,
            isError = errorEstatura != null,
            supportingText = errorEstatura?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = onCalcular,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Calcular IMC")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PantallaFormularioPreview() {
    ImcSarayTheme {
        PantallaFormulario(
            nombre = "", onNombreChange = {},
            edad = "10,4", onEdadChange = {},
            sexo = null, onSexoChange = {},
            peso = "abc", onPesoChange = {},
            estatura = "", onEstaturaChange = {},
            mostrarErrores = true, onCalcular = {},
        )
    }
}
