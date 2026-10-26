
package com.example.recordatoriosapp

import com.example.recordatoriosapp.model.Categoria
import com.example.recordatoriosapp.model.EstadoRecordatorio
import com.example.recordatoriosapp.model.Prioridad
import com.example.recordatoriosapp.model.Recordatorio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import com.example.recordatoriosapp.model.SimulacionDeDatos

class ExampleUnitTest {

    private val categoriaMantenimiento = Categoria(
        id = "CAT-01",
        nombre = "Mantenimiento"
    )

    @Test
    fun recordatorioValido_seCreaCorrectamente() {
        val recordatorio = Recordatorio(
            id = "REC-01",
            titulo = "Revisar tablero electrico",
            categoria = categoriaMantenimiento,
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Pendiente
        )

        assertEquals("Revisar tablero electrico", recordatorio.titulo)
    }

    @Test
    fun tituloVacio_esRechazado() {
        assertThrows(IllegalArgumentException::class.java) {
            Recordatorio(
                id = "REC-02",
                titulo = "   ",
                categoria = categoriaMantenimiento,
                prioridad = Prioridad.Media,
                estado = EstadoRecordatorio.Pendiente
            )
        }
    }

    @Test
    fun identificadorVacio_esRechazado() {
        assertThrows(IllegalArgumentException::class.java) {
            Recordatorio(
                id = "",
                titulo = "Revisar tablero electrico",
                categoria = categoriaMantenimiento,
                prioridad = Prioridad.Baja,
                estado = EstadoRecordatorio.Pendiente
            )
        }
    }

    @Test
    fun listaContieneCincoRecordatorios() {
        assertEquals(
            5,
            SimulacionDeDatos.recordatorios.size
        )
    }

    @Test
    fun categoriasNoEstanDuplicadas() {
        assertEquals(
            3,
            SimulacionDeDatos.categoriasUnicas.size
        )
    }

    @Test
    fun categoriaMantenimientoTieneColorAsignado() {
        assertEquals(
            "#2563EB",
            SimulacionDeDatos.coloresCategorias["CAT-01"]
        )
    }

    @Test
    fun filtrosDevuelvenResultadosEsperados() {
        assertEquals(
            3,
            SimulacionDeDatos.recordatoriosPendientes.size
        )

        assertEquals(
            2,
            SimulacionDeDatos.recordatoriosPrioridadAlta.size
        )

        assertEquals(
            1,
            SimulacionDeDatos.recordatoriosPendientesPrioridadAlta.size
        )
    }

}
