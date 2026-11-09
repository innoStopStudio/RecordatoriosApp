
package com.example.recordatoriosapp.data.mapper

import com.example.recordatoriosapp.data.local.RecordatorioEntity
import com.example.recordatoriosapp.data.remote.RecordatorioDto

fun RecordatorioEntity.toDto(
    usuarioId: String
): RecordatorioDto {
    return RecordatorioDto(
        id = id,
        usuarioId = usuarioId,
        titulo = titulo,
        descripcion = descripcion,
        categoria = categoria,
        prioridad = prioridad,
        estado = estado,
        fechaAviso = fechaAviso,
        ubicacion = ubicacion
    )
}

fun RecordatorioDto.toLocalEntity(): RecordatorioEntity {
    return RecordatorioEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        categoria = categoria,
        prioridad = prioridad,
        estado = estado,
        fechaAviso = fechaAviso,
        ubicacion = ubicacion,
        pendienteSincronizacion = false,
        usuarioId = usuarioId,
        estadoSincronizacion = "SINCRONIZADO",
        versionLocal = 1L,
        eliminado = false
    )
}