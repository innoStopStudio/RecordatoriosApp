
package com.example.recordatoriosapp.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recordatoriosapp.data.EstadoSincronizacion

@Composable
fun IndicadorSincronizacion(
    estado: EstadoSincronizacion,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (estado == EstadoSincronizacion.SinComprobar) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = when (estado) {
                EstadoSincronizacion.SinComprobar -> ""
                EstadoSincronizacion.Sincronizando ->
                    "Sincronizando..."
                EstadoSincronizacion.Sincronizado ->
                    "Sincronizado"
                is EstadoSincronizacion.Pendiente ->
                    estado.mensaje
                is EstadoSincronizacion.Error ->
                    estado.mensaje
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (estado is EstadoSincronizacion.Error) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.weight(1f)
        )

        if (
            estado is EstadoSincronizacion.Error ||
            estado is EstadoSincronizacion.Pendiente
        ) {
            TextButton(onClick = onReintentar) {
                Text("Reintentar")
            }
        }
    }
}