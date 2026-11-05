
package com.example.recordatoriosapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.recordatoriosapp.model.*
import com.example.recordatoriosapp.data.RecordatorioRepository
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrear(
    viewModel: CreacionViewModel,
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.guardadoExitoso) {
        if (estado.guardadoExitoso) {
            onGuardado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo recordatorio") },
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
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = estado.titulo,
                onValueChange = viewModel::cambiarTitulo,
                label = { Text("Título") },
                isError = estado.titulo.isNotEmpty() &&
                        !estado.tituloValido,
                supportingText = {
                    if (!estado.tituloValido) {
                        Text("Mínimo 4 caracteres")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Título del recordatorio"
                    }
            )

            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = viewModel::cambiarDescripcion,
                label = { Text("Descripción (opcional)") },
                isError = !estado.descripcionValida,
                supportingText = {
                    Text("${estado.descripcion.length}/200 caracteres")
                    if (!estado.descripcionValida) {
                        Text("La descripción supera los 200 caracteres")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Categoría")
            listOf(
                "Mantenimiento",
                "Operaciones",
                "Administración"
            ).forEach { opcion ->
                Row {
                    RadioButton(
                        selected = estado.categoria == opcion,
                        onClick = {
                            viewModel.cambiarCategoria(opcion)
                        }
                    )
                    TextButton(
                        onClick = {
                            viewModel.cambiarCategoria(opcion)
                        }
                    ) {
                        Text(opcion)
                    }
                }
            }

            Text("Prioridad")
            listOf("Baja", "Media", "Alta").forEach { opcion ->
                Row {
                    RadioButton(
                        selected = estado.prioridad == opcion,
                        onClick = {
                            viewModel.cambiarPrioridad(opcion)
                        }
                    )
                    TextButton(
                        onClick = {
                            viewModel.cambiarPrioridad(opcion)
                        }
                    ) {
                        Text(opcion)
                    }
                }
            }

            estado.errorGuardado?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                enabled = estado.formularioValido &&
                        !estado.enviando &&
                        !estado.guardadoExitoso,
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::guardar
            ) {
                if (estado.enviando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar recordatorio")
                }
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth(),
                enabled = !estado.enviando
            ) {
                Text("Cancelar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    recordatorio: Recordatorio?,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del recordatorio") },
                navigationIcon = {
                    TextButton(onClick = onVolver) { Text("Volver") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (recordatorio == null) {
                Text("No se encontró el recordatorio.")
            } else {
                Text(
                    recordatorio.titulo,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text("Descripción: ${recordatorio.descripcion ?: "Sin descripción"}")
                Text("Categoría: ${recordatorio.categoria.nombre}")
                Text("Prioridad: ${when (recordatorio.prioridad) {
                    Prioridad.Alta -> "Alta"
                    Prioridad.Media -> "Media"
                    Prioridad.Baja -> "Baja"
                }}")
                Text("Estado: ${when (recordatorio.estado) {
                    EstadoRecordatorio.Pendiente -> "Pendiente"
                    EstadoRecordatorio.Completado -> "Completado"
                    EstadoRecordatorio.Cancelado -> "Cancelado"
                }}")
                Text("Fecha de aviso: ${recordatorio.fechaAviso ?: "No definida"}")
                Text("Ubicación: ${recordatorio.ubicacion ?: "No definida"}")
            }
        }
    }
}
