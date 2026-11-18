package com.sarayasecas.imc_saray

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import com.sarayasecas.imc_saray.ui.theme.BandaSeigaiha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sarayasecas.imc_saray.modelo.Persona
import com.sarayasecas.imc_saray.modelo.Sexo
import com.sarayasecas.imc_saray.modelo.leerNumero
import com.sarayasecas.imc_saray.modelo.validarEdad
import com.sarayasecas.imc_saray.modelo.validarEstatura
import com.sarayasecas.imc_saray.modelo.validarNombre
import com.sarayasecas.imc_saray.modelo.validarPeso
import com.sarayasecas.imc_saray.modelo.validarSexo
import com.sarayasecas.imc_saray.ui.theme.ImcSarayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImcSarayTheme {
                App()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    // Estado del formulario (rememberSaveable: sobrevive a girar la pantalla)
    var nombre by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var sexo by rememberSaveable { mutableStateOf<Sexo?>(null) }
    var peso by rememberSaveable { mutableStateOf("") }
    var estatura by rememberSaveable { mutableStateOf("") }
    var mostrarErrores by rememberSaveable { mutableStateOf(false) }
    var enResultado by rememberSaveable { mutableStateOf(false) }

    val formularioValido = validarNombre(nombre) == null && validarEdad(edad) == null && validarSexo(sexo) == null &&
        validarPeso(peso) == null && validarEstatura(estatura) == null

    // El botón "atrás" del sistema también vuelve al formulario
    BackHandler(enabled = enResultado) { enResultado = false }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (enResultado) "Resultado" else "Calculadora IMC")
                            Text(
                                if (enResultado) "結果" else "体格指数",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
                BandaSeigaiha()
            }
        },
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        if (enResultado && formularioValido) {
            val persona = Persona(
                nombre = nombre.trim(),
                edad = edad.trim().toInt(),
                sexo = sexo!!,
                pesoKg = leerNumero(peso)!!,
                estaturaCm = leerNumero(estatura)!!,
            )
            PantallaResultado(persona = persona, onVolver = { enResultado = false }, modifier = modifier)
        } else {
            PantallaFormulario(
                nombre = nombre, onNombreChange = { nombre = it },
                edad = edad, onEdadChange = { edad = it },
                sexo = sexo, onSexoChange = { sexo = it },
                peso = peso, onPesoChange = { peso = it },
                estatura = estatura, onEstaturaChange = { estatura = it },
                mostrarErrores = mostrarErrores,
                onCalcular = {
                    mostrarErrores = true
                    if (formularioValido) enResultado = true
                },
                modifier = modifier,
            )
        }
    }
}
