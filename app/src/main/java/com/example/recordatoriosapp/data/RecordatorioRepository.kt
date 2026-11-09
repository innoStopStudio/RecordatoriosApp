
package com.example.recordatoriosapp.data

import com.example.recordatoriosapp.model.Recordatorio
import kotlinx.coroutines.flow.Flow

interface RecordatorioRepository {

    // Consultas
    fun observarRecordatorios(): Flow<List<Recordatorio>>

    suspend fun obtenerRecordatorios(): List<Recordatorio>

    // Crear
    suspend fun guardarRecordatorio(
        recordatorio: Recordatorio
    )

    // Editar
    suspend fun actualizarRecordatorio(
        recordatorio: Recordatorio
    )

    // Eliminar
    suspend fun eliminarRecordatorio(
        id: String
    )
}