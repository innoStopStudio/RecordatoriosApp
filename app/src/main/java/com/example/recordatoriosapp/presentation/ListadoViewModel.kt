package com.example.recordatoriosapp.presentation

import androidx.lifecycle.ViewModel
import com.example.recordatoriosapp.data.RecordatorioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ListadoViewModel(
    private val repository: RecordatorioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListadoUiState>(
        ListadoUiState.Cargando
    )

    val uiState: StateFlow<ListadoUiState> = _uiState

    init {
        cargarRecordatorios()
    }
    fun cargarRecordatorios() {
        viewModelScope.launch {
            _uiState.value = ListadoUiState.Cargando

            try {
                val recordatorios = repository.obtenerRecordatorios()

                _uiState.value = if (recordatorios.isEmpty()) {
                    ListadoUiState.Vacio
                } else {
                    ListadoUiState.ConDatos(recordatorios)
                }

            } catch (e: Exception) {
                _uiState.value = ListadoUiState.Error(
                    "No se pudieron cargar los recordatorios."
                )
            }
        }
    }
}