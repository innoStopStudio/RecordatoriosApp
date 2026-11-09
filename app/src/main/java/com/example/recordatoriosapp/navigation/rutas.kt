package com.example.recordatoriosapp.navigation

object Rutas {
    const val LISTADO = "listado"
    const val CREAR = "crear"
    const val DETALLE = "detalle/{id}"
    const val PREFERENCIAS = "preferencias"
    const val LOGIN = "login"
    fun detalle(id: String): String {
        return "detalle/$id"
    }
}