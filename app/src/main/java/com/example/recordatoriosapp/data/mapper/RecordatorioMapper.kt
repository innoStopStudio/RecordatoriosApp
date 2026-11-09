
package com.example.recordatoriosapp.data.mapper

import com.example.recordatoriosapp.data.local.RecordatorioEntity
import com.example.recordatoriosapp.model.Categoria
import com.example.recordatoriosapp.model.EstadoRecordatorio
import com.example.recordatoriosapp.model.Prioridad
import com.example.recordatoriosapp.model.Recordatorio


fun Recordatorio.toEntity(
    pendienteSincronizacion: Boolean = true,
    usuarioId: String? = null
): RecordatorioEntity {

    val prioridadTexto = when (prioridad) {
        Prioridad.Baja -> "BAJA"
        Prioridad.Media -> "MEDIA"
        Prioridad.Alta -> "ALTA"
    }

    val estadoTexto = when (estado) {
        EstadoRecordatorio.Pendiente -> "PENDIENTE"
        EstadoRecordatorio.Completado -> "COMPLETADO"
        EstadoRecordatorio.Cancelado -> "CANCELADO"
    }

    return RecordatorioEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        categoria = categoria.id,
        prioridad = prioridadTexto,
        estado = estadoTexto,
        fechaAviso = fechaAviso,
        ubicacion = ubicacion,
        pendienteSincronizacion = pendienteSincronizacion,
        usuarioId = usuarioId
    )
}

fun RecordatorioEntity.toDomain(): Recordatorio {

    val prioridadDominio = when (prioridad) {
        "BAJA" -> Prioridad.Baja
        "MEDIA" -> Prioridad.Media
        "ALTA" -> Prioridad.Alta
        else -> Prioridad.Media
    }

    val estadoDominio = when (estado) {
        "PENDIENTE" -> EstadoRecordatorio.Pendiente
        "COMPLETADO" -> EstadoRecordatorio.Completado
        "CANCELADO" -> EstadoRecordatorio.Cancelado
        else -> EstadoRecordatorio.Pendiente
    }

    return Recordatorio(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        categoria = Categoria(
            id = categoria,
            nombre = categoria
        ),
        prioridad = prioridadDominio,
        estado = estadoDominio,
        fechaAviso = fechaAviso,
        ubicacion = ubicacion
    )
}