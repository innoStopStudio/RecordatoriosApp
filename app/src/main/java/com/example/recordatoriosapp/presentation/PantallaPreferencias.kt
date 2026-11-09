
package com.example.recordatoriosapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recordatoriosapp.data.preferences.OrdenRecordatorios

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPreferencias(
    viewModel: PreferenciasViewModel,
    onVolver: () -> Unit
) {
    val preferencias by viewModel.preferencias.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferencias") },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Apariencia",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tema oscuro")
                Switch(
                    checked = preferencias.temaOscuro,
                    onCheckedChange = viewModel::cambiarTemaOscuro
                )
            }

            HorizontalDivider()

            Text(
                "Orden del listado",
                style = MaterialTheme.typography.titleMedium
            )

            listOf(
                OrdenRecordatorios.TITULO to "Por título",
                OrdenRecordatorios.PRIORIDAD to "Por prioridad"
            ).forEach { (orden, etiqueta) ->
                Row {
                    RadioButton(
                        selected = preferencias.orden == orden,
                        onClick = { viewModel.cambiarOrden(orden) }
                    )
                    TextButton(
                        onClick = { viewModel.cambiarOrden(orden) }
                    ) {
                        Text(etiqueta)
                    }
                }
            }

            HorizontalDivider()

            Text(
                "Visualización",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Mostrar completados",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = preferencias.mostrarCompletados,
                    onCheckedChange = viewModel::cambiarMostrarCompletados
                )
            }
        }
    }
}