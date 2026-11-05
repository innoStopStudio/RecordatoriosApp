package com.example.recordatoriosapp.presentation

import com.example.recordatoriosapp.model.Recordatorio

sealed interface ListadoUiState {

    data object Cargando : ListadoUiState

    data object Vacio : ListadoUiState

    data class ConDatos(
        val recordatorios: List<Recordatorio>
    ) : ListadoUiState

    data class Error(
        val mensaje: String
    ) : ListadoUiState
}