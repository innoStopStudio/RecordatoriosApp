package com.example.recordatoriosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recordatoriosapp.data.RecordatorioRepository

class ListadoViewModelFactory(
    private val repository: RecordatorioRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListadoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListadoViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}