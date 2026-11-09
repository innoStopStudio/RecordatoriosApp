
package com.example.recordatoriosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recordatoriosapp.data.preferences.OrdenRecordatorios
import com.example.recordatoriosapp.data.preferences.PreferenciasRepository
import com.example.recordatoriosapp.data.preferences.PreferenciasUsuario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferenciasViewModel(
    private val repository: PreferenciasRepository
) : ViewModel() {

    val preferencias: StateFlow<PreferenciasUsuario> =
        repository.preferencias.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PreferenciasUsuario()
        )

    fun cambiarTemaOscuro(activado: Boolean) {
        viewModelScope.launch {
            repository.cambiarTemaOscuro(activado)
        }
    }

    fun cambiarOrden(orden: OrdenRecordatorios) {
        viewModelScope.launch {
            repository.cambiarOrden(orden)
        }
    }

    fun cambiarMostrarCompletados(mostrar: Boolean) {
        viewModelScope.launch {
            repository.cambiarMostrarCompletados(mostrar)
        }
    }
}

class PreferenciasViewModelFactory(
    private val repository: PreferenciasRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(
            modelClass.isAssignableFrom(PreferenciasViewModel::class.java)
        )
        return PreferenciasViewModel(repository) as T
    }
}