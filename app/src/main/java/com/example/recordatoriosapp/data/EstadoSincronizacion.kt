
package com.example.recordatoriosapp.data

sealed interface EstadoSincronizacion {
    data object SinComprobar : EstadoSincronizacion
    data object Sincronizando : EstadoSincronizacion
    data object Sincronizado : EstadoSincronizacion
    data class Pendiente(val mensaje: String) : EstadoSincronizacion
    data class Error(val mensaje: String) : EstadoSincronizacion
}