
package com.example.recordatoriosapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EstadoListado(
    mensaje: String,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    textoBoton: String? = null,
    onAccion: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator()
            }

            if (mensaje.isNotBlank()) {
                Text(mensaje)
            }

            if (textoBoton != null && !cargando) {
                Button(onClick = onAccion) {
                    Text(textoBoton)
                }
            }
        }
    }
}