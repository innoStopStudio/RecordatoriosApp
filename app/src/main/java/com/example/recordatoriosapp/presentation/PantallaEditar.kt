
package com.example.recordatoriosapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recordatoriosapp.data.RecordatorioRepository
import com.example.recordatoriosapp.model.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditar(
    recordatorio: Recordatorio?,
    repository: RecordatorioRepository,
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {
    if (recordatorio == null) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("No se encontró el recordatorio.")
            TextButton(onClick = onVolver) {
                Text("Volver")
            }
        }
        return
    }

    var titulo by remember(recordatorio.id) {
        mutableStateOf(recordatorio.titulo)
    }
    var descripcion by remember(recordatorio.id) {
        mutableStateOf(recordatorio.descripcion.orEmpty())
    }
    var categoria by remember(recordatorio.id) {
        mutableStateOf(recordatorio.categoria.id)
    }
    var prioridad by remember(recordatorio.id) {
        mutableStateOf(recordatorio.prioridad)
    }
    var estado by remember(recordatorio.id) {
        mutableStateOf(recordatorio.estado)
    }
    var fechaAviso by remember(recordatorio.id) {
        mutableStateOf(recordatorio.fechaAviso.orEmpty())
    }
    var ubicacion by remember(recordatorio.id) {
        mutableStateOf(recordatorio.ubicacion.orEmpty())
    }

    var mostrarFecha by remember { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val formularioValido =
        titulo.trim().length >= 4 &&
                descripcion.length <= 200 &&
                ubicacion.length <= 120 &&
                categoria.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar recordatorio") },
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
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                isError = titulo.isNotEmpty() &&
                        titulo.trim().length < 4,
                supportingText = {
                    Text("Mínimo 4 caracteres")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                isError = descripcion.length > 200,
                supportingText = {
                    Text("${descripcion.length}/200 caracteres")
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
                        selected = categoria == opcion,
                        onClick = { categoria = opcion }
                    )
                    TextButton(onClick = { categoria = opcion }) {
                        Text(opcion)
                    }
                }
            }

            Text("Prioridad")
            listOf(
                "Baja" to Prioridad.Baja,
                "Media" to Prioridad.Media,
                "Alta" to Prioridad.Alta
            ).forEach { (nombre, valor) ->
                Row {
                    RadioButton(
                        selected = prioridad == valor,
                        onClick = { prioridad = valor }
                    )
                    TextButton(onClick = { prioridad = valor }) {
                        Text(nombre)
                    }
                }
            }

            Text("Estado")
            listOf(
                "Pendiente" to EstadoRecordatorio.Pendiente,
                "Completado" to EstadoRecordatorio.Completado,
                "Cancelado" to EstadoRecordatorio.Cancelado
            ).forEach { (nombre, valor) ->
                Row {
                    RadioButton(
                        selected = estado == valor,
                        onClick = { estado = valor }
                    )
                    TextButton(onClick = { estado = valor }) {
                        Text(nombre)
                    }
                }
            }

            OutlinedTextField(
                value = fechaAviso,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de aviso (opcional)") },
                trailingIcon = {
                    TextButton(
                        onClick = { mostrarFecha = true }
                    ) {
                        Text("Elegir")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (mostrarFecha) {
                val datePickerState = rememberDatePickerState()

                DatePickerDialog(
                    onDismissRequest = { mostrarFecha = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    fechaAviso = Instant
                                        .ofEpochMilli(it)
                                        .atZone(ZoneOffset.UTC)
                                        .toLocalDate()
                                        .toString()
                                }
                                mostrarFecha = false
                            }
                        ) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { mostrarFecha = false }
                        ) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (fechaAviso.isNotBlank()) {
                TextButton(onClick = { fechaAviso = "" }) {
                    Text("Quitar fecha")
                }
            }

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación (opcional)") },
                isError = ubicacion.length > 120,
                supportingText = {
                    Text("${ubicacion.length}/120 caracteres")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    if (!guardando && formularioValido) {
                        guardando = true
                        error = null

                        scope.launch {
                            try {
                                repository.actualizarRecordatorio(
                                    recordatorio.copy(
                                        titulo = titulo.trim(),
                                        descripcion = descripcion.trim()
                                            .ifBlank { null },
                                        categoria = Categoria(
                                            id = categoria,
                                            nombre = categoria
                                        ),
                                        prioridad = prioridad,
                                        estado = estado,
                                        fechaAviso = fechaAviso
                                            .ifBlank { null },
                                        ubicacion = ubicacion.trim()
                                            .ifBlank { null }
                                    )
                                )
                                onGuardado()
                            } catch (e: Exception) {
                                error = "No se pudo guardar el cambio."
                            } finally {
                                guardando = false
                            }
                        }
                    }
                },
                enabled = formularioValido && !guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (guardando) "Guardando..."
                    else "Guardar cambios"
                )
            }

            OutlinedButton(
                onClick = onVolver,
                enabled = !guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}