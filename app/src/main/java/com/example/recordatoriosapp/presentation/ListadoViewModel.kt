
package com.example.recordatoriosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatoriosapp.data.RecordatorioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListadoViewModel(
    private val repository: RecordatorioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListadoUiState>(
        ListadoUiState.Cargando
    )

    val uiState: StateFlow<ListadoUiState> = _uiState

    private var observacionJob: Job? = null

    init {
        cargarRecordatorios()
    }

    fun cargarRecordatorios() {
        observacionJob?.cancel()

        observacionJob = viewModelScope.launch {
            _uiState.value = ListadoUiState.Cargando

            try {
                repository.observarRecordatorios().collect { recordatorios ->
                    _uiState.value = if (recordatorios.isEmpty()) {
                        ListadoUiState.Vacio
                    } else {
                        ListadoUiState.ConDatos(recordatorios)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = ListadoUiState.Error(
                    "No se pudieron cargar los recordatorios."
                )
            }
        }
    }
}