
package com.example.recordatoriosapp.presentation

data class CreacionUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "Mantenimiento",
    val prioridad: String = "Media",
    val fechaAviso: String = "",
    val ubicacion: String = "",
    val enviando: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val errorGuardado: String? = null
) {
    val tituloValido: Boolean
        get() = titulo.trim().length >= 4

    val descripcionValida: Boolean
        get() = descripcion.length <= 200

    val categoriaValida: Boolean
        get() = categoria in listOf(
            "Mantenimiento",
            "Operaciones",
            "Administración"
        )

    val prioridadValida: Boolean
        get() = prioridad in listOf("Baja", "Media", "Alta")

    val ubicacionValida: Boolean
        get() = ubicacion.length <= 120

    val formularioValido: Boolean
        get() = tituloValido &&
                descripcionValida &&
                categoriaValida &&
                prioridadValida &&
                ubicacionValida
}