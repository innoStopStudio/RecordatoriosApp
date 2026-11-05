package com.example.recordatoriosapp.navigation

object Rutas {
    const val LISTADO = "listado"
    const val CREAR = "crear"
    const val DETALLE = "detalle/{id}"

    fun detalle(id: String): String {
        return "detalle/$id"
    }
}