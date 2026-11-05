
package com.example.recordatoriosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatoriosapp.data.RecordatorioRepository
import com.example.recordatoriosapp.model.Categoria
import com.example.recordatoriosapp.model.EstadoRecordatorio
import com.example.recordatoriosapp.model.Prioridad
import com.example.recordatoriosapp.model.Recordatorio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CreacionViewModel(
    private val repository: RecordatorioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreacionUiState())
    val uiState: StateFlow<CreacionUiState> = _uiState

    fun cambiarTitulo(valor: String) {
        _uiState.update { it.copy(titulo = valor) }
    }

    fun cambiarDescripcion(valor: String) {
        _uiState.update { it.copy(descripcion = valor) }
    }

    fun cambiarCategoria(valor: String) {
        _uiState.update { it.copy(categoria = valor) }
    }

    fun cambiarPrioridad(valor: String) {
        _uiState.update { it.copy(prioridad = valor) }
    }

    fun guardar() {
        val estado = _uiState.value

        if (!estado.formularioValido ||
            estado.enviando ||
            estado.guardadoExitoso
        ) return

        _uiState.update {
            it.copy(enviando = true, errorGuardado = null)
        }

        viewModelScope.launch {
            try {
                delay(700)

                val prioridadElegida = when (estado.prioridad) {
                    "Alta" -> Prioridad.Alta
                    "Baja" -> Prioridad.Baja
                    else -> Prioridad.Media
                }

                val recordatorio = Recordatorio(
                    id = UUID.randomUUID().toString(),
                    titulo = estado.titulo.trim(),
                    descripcion = estado.descripcion.trim()
                        .ifBlank { null },
                    categoria = Categoria(
                        id = estado.categoria,
                        nombre = estado.categoria
                    ),
                    prioridad = prioridadElegida,
                    estado = EstadoRecordatorio.Pendiente
                )

                repository.guardarRecordatorio(recordatorio)

                _uiState.update {
                    it.copy(
                        enviando = false,
                        guardadoExitoso = true
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        enviando = false,
                        errorGuardado =
                            "No se pudo guardar el recordatorio. Reintentá."
                    )
                }
            }
        }
    }
}
