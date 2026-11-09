
package com.example.recordatoriosapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordatorioDao {

    @Query("SELECT * FROM recordatorios")
    fun observarTodos(): Flow<List<RecordatorioEntity>>

    @Query("SELECT * FROM recordatorios WHERE id = :id")
    fun observarPorId(id: String): Flow<RecordatorioEntity?>

    @Query("SELECT * FROM recordatorios WHERE estado = :estado")
    fun observarPorEstado(
        estado: String
    ): Flow<List<RecordatorioEntity>>

    @Query("SELECT * FROM recordatorios WHERE prioridad = :prioridad")
    fun observarPorPrioridad(
        prioridad: String
    ): Flow<List<RecordatorioEntity>>

    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE usuarioId = :usuarioId AND eliminado = 0"
    )
    fun observarDelUsuario(
        usuarioId: String
    ): Flow<List<RecordatorioEntity>>

    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE usuarioId = :usuarioId " +
                "AND estadoSincronizacion != 'SINCRONIZADO'"
    )
    suspend fun obtenerPendientesDelUsuario(
        usuarioId: String
    ): List<RecordatorioEntity>

    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE pendienteSincronizacion = 1"
    )
    suspend fun obtenerPendientes(): List<RecordatorioEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(recordatorio: RecordatorioEntity)

    @Update
    suspend fun actualizar(recordatorio: RecordatorioEntity)

    @Delete
    suspend fun eliminar(recordatorio: RecordatorioEntity)

    @Query(
        """
        UPDATE recordatorios
        SET titulo = :titulo,
            descripcion = :descripcion,
            categoria = :categoria,
            prioridad = :prioridad,
            estado = :estado,
            fechaAviso = :fechaAviso,
            ubicacion = :ubicacion,
            versionLocal = versionLocal + 1,
            pendienteSincronizacion = 1,
            estadoSincronizacion =
                CASE
                    WHEN estadoSincronizacion = 'PENDIENTE_CREAR'
                    THEN 'PENDIENTE_CREAR'
                    ELSE 'PENDIENTE_ACTUALIZAR'
                END
        WHERE id = :id
          AND usuarioId = :usuarioId
          AND eliminado = 0
        """
    )
    suspend fun actualizarLocal(
        id: String,
        usuarioId: String,
        titulo: String,
        descripcion: String?,
        categoria: String,
        prioridad: String,
        estado: String,
        fechaAviso: String?,
        ubicacion: String?
    ): Int

    @Query(
        """
        UPDATE recordatorios
        SET eliminado = 1,
            versionLocal = versionLocal + 1,
            pendienteSincronizacion = 1,
            estadoSincronizacion = 'PENDIENTE_ELIMINAR'
        WHERE id = :id
          AND usuarioId = :usuarioId
          AND eliminado = 0
        """
    )
    suspend fun marcarEliminado(
        id: String,
        usuarioId: String
    ): Int

    @Query(
        """
        UPDATE recordatorios
        SET pendienteSincronizacion = 0,
            estadoSincronizacion = 'SINCRONIZADO'
        WHERE id = :id
          AND usuarioId = :usuarioId
          AND versionLocal = :version
          AND estadoSincronizacion = :estadoEsperado
        """
    )
    suspend fun confirmarSincronizacion(
        id: String,
        usuarioId: String,
        version: Long,
        estadoEsperado: String
    ): Int

    @Query(
        """
        DELETE FROM recordatorios
        WHERE id = :id
          AND usuarioId = :usuarioId
          AND versionLocal = :version
          AND estadoSincronizacion = 'PENDIENTE_ELIMINAR'
        """
    )
    suspend fun confirmarEliminacion(
        id: String,
        usuarioId: String,
        version: Long
    ): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarDesdeRemoto(
        recordatorio: RecordatorioEntity
    ): Long

    @Query(
        """
        UPDATE recordatorios
        SET titulo = :titulo,
            descripcion = :descripcion,
            categoria = :categoria,
            prioridad = :prioridad,
            estado = :estado,
            fechaAviso = :fechaAviso,
            ubicacion = :ubicacion
        WHERE id = :id
          AND usuarioId = :usuarioId
          AND estadoSincronizacion = 'SINCRONIZADO'
          AND eliminado = 0
        """
    )
    suspend fun actualizarDesdeRemoto(
        id: String,
        usuarioId: String,
        titulo: String,
        descripcion: String?,
        categoria: String,
        prioridad: String,
        estado: String,
        fechaAviso: String?,
        ubicacion: String?
    ): Int

    @Transaction
    suspend fun incorporarDesdeRemoto(
        recordatorio: RecordatorioEntity
    ) {
        val insertado = insertarDesdeRemoto(recordatorio)

        if (insertado == -1L) {
            actualizarDesdeRemoto(
                id = recordatorio.id,
                usuarioId = recordatorio.usuarioId ?: return,
                titulo = recordatorio.titulo,
                descripcion = recordatorio.descripcion,
                categoria = recordatorio.categoria,
                prioridad = recordatorio.prioridad,
                estado = recordatorio.estado,
                fechaAviso = recordatorio.fechaAviso,
                ubicacion = recordatorio.ubicacion
            )
        }
    }
}