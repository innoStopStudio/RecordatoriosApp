
package com.example.recordatoriosapp.data.preferences

enum class OrdenRecordatorios {
    TITULO,
    PRIORIDAD
}

data class PreferenciasUsuario(
    val temaOscuro: Boolean = false,
    val orden: OrdenRecordatorios = OrdenRecordatorios.TITULO,
    val mostrarCompletados: Boolean = true
)