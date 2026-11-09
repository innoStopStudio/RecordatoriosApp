
package com.example.recordatoriosapp.data.remote

import com.google.gson.annotations.SerializedName

data class RecordatorioDto(
    val id: String,

    @SerializedName("usuario_id")
    val usuarioId: String,

    val titulo: String,
    val descripcion: String?,
    val categoria: String,
    val prioridad: String,
    val estado: String,

    @SerializedName("fecha_aviso")
    val fechaAviso: String?,

    val ubicacion: String?
)