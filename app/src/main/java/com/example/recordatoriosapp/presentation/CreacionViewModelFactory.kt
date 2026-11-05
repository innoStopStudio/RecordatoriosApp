
package com.example.recordatoriosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recordatoriosapp.data.RecordatorioRepository

class CreacionViewModelFactory(
    private val repository: RecordatorioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreacionViewModel::class.java)) {
            return CreacionViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
