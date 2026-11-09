
package com.example.recordatoriosapp.data

import com.example.recordatoriosapp.model.Recordatorio
import com.example.recordatoriosapp.model.SimulacionDeDatos
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RecordatorioRepositoryMemoria : RecordatorioRepository {

    private val recordatorios = MutableStateFlow(
        SimulacionDeDatos.recordatorios.toList()
    )

    override fun observarRecordatorios(): Flow<List<Recordatorio>> =
        recordatorios.asStateFlow()

    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        delay(1000)
        return recordatorios.value
    }

    override suspend fun guardarRecordatorio(recordatorio: Recordatorio) {
        recordatorios.update { actuales ->
            actuales + recordatorio
        }
    }

    override suspend fun actualizarRecordatorio(
        recordatorio: Recordatorio
    ) {
        recordatorios.value = recordatorios.value.map { actual ->
            if (actual.id == recordatorio.id) {
                recordatorio
            } else {
                actual
            }
        }
    }

    override suspend fun eliminarRecordatorio(id: String) {
        recordatorios.value = recordatorios.value.filterNot { actual ->
            actual.id == id
        }
    }

}