package com.example.recordatoriosapp.data

import com.example.recordatoriosapp.model.Recordatorio
import com.example.recordatoriosapp.model.SimulacionDeDatos
import kotlinx.coroutines.delay

class RecordatorioRepositoryMemoria : RecordatorioRepository {

    private val recordatorios =
        SimulacionDeDatos.recordatorios.toMutableList()

    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        delay(1000)
        return recordatorios.toList()
    }

    override suspend fun guardarRecordatorio(recordatorio: Recordatorio) {
        recordatorios.add(recordatorio)
    }
}