package com.example.recordatoriosapp.data
import com.example.recordatoriosapp.model.Recordatorio

interface RecordatorioRepository {

    suspend fun obtenerRecordatorios(): List<Recordatorio>

    suspend fun guardarRecordatorio(recordatorio: Recordatorio)
}