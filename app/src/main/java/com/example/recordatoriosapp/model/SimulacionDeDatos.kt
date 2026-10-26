package com.example.recordatoriosapp.model

object SimulacionDeDatos {

    val mantenimiento = Categoria(
        id = "CAT-01",
        nombre = "Mantenimiento"
    )

    val operaciones = Categoria(
        id = "CAT-02",
        nombre = "Operaciones"
    )

    val administracion = Categoria(
        id = "CAT-03",
        nombre = "Administración"
    )

    val recordatorios = listOf(
        Recordatorio(
            id = "REC-01",
            titulo = "Revisar tablero eléctrico",
            descripcion = "Verificar el estado del tablero principal",
            categoria = mantenimiento,
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Pendiente
        ),
        Recordatorio(
            id = "REC-02",
            titulo = "Inspeccionar instalación de cliente",
            categoria = operaciones,
            prioridad = Prioridad.Media,
            estado = EstadoRecordatorio.Pendiente
        ),
        Recordatorio(
            id = "REC-03",
            titulo = "Presentar informe de servicios",
            categoria = administracion,
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Completado
        ),
        Recordatorio(
            id = "REC-04",
            titulo = "Realizar mantenimiento de equipos",
            categoria = mantenimiento,
            prioridad = Prioridad.Baja,
            estado = EstadoRecordatorio.Pendiente
        ),
        Recordatorio(
            id = "REC-05",
            titulo = "Coordinar visita técnica",
            categoria = operaciones,
            prioridad = Prioridad.Media,
            estado = EstadoRecordatorio.Cancelado
        )
    )

    val categoriasUnicas: Set<Categoria> =
        recordatorios.map { it.categoria }.toSet()

    val coloresCategorias: Map<String, String> = mapOf(
        "CAT-01" to "#2563EB",
        "CAT-02" to "#16A34A",
        "CAT-03" to "#9333EA"
    )

    val recordatoriosPendientes = recordatorios.filter {
        it.estado == EstadoRecordatorio.Pendiente
    }

    val recordatoriosPrioridadAlta = recordatorios.filter {
        it.prioridad == Prioridad.Alta
    }

    val recordatoriosPendientesPrioridadAlta = recordatorios.filter {
        it.estado == EstadoRecordatorio.Pendiente &&
                it.prioridad == Prioridad.Alta
    }
}