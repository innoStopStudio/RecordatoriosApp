
package com.example.recordatoriosapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordatorios")
data class RecordatorioEntity(
    @PrimaryKey
    val id: String,
    val titulo: String,
    val descripcion: String?,
    val categoria: String,
    val prioridad: String,
    val estado: String,
    val fechaAviso: String?,
    val ubicacion: String?,
    val pendienteSincronizacion: Boolean = true,
    val usuarioId: String? = null,
    val estadoSincronizacion: String = "PENDIENTE_CREAR",
    val versionLocal: Long = 1L,
    val eliminado: Boolean = false
)
