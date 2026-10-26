package com.example.recordatoriosapp.model

data class Categoria(
    val id: String,
    val nombre: String
)

sealed class Prioridad {
    data object Baja : Prioridad()
    data object Media : Prioridad()
    data object Alta : Prioridad()
}

sealed class EstadoRecordatorio {
    data object Pendiente : EstadoRecordatorio()
    data object Completado : EstadoRecordatorio()
    data object Cancelado : EstadoRecordatorio()
}

data class Recordatorio(
    val id: String,
    val titulo: String,
    val descripcion: String? = null,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio,
    val fechaAviso: String? = null,
    val ubicacion: String? = null
) {

    init {
        require(id.isNotBlank()) {
            "El identificador no puede estar vacío"
        }

        require(titulo.isNotBlank()) {
            "El título no puede estar vacío"
        }
    }
}