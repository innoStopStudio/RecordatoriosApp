
package com.example.recordatoriosapp.data

import com.example.recordatoriosapp.BuildConfig
import com.example.recordatoriosapp.data.local.RecordatorioDao
import com.example.recordatoriosapp.data.mapper.toDto
import com.example.recordatoriosapp.data.mapper.toLocalEntity
import com.example.recordatoriosapp.data.remote.RetrofitClient
import com.example.recordatoriosapp.data.remote.SesionUsuario
import java.io.IOException

class SincronizadorRecordatorios(
    private val dao: RecordatorioDao
) {

    private val api = RetrofitClient.crearRecordatorios(
        baseUrl = BuildConfig.SUPABASE_URL,
        publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
    )

    suspend fun sincronizar(sesion: SesionUsuario) {
        enviarPendientes(sesion)
        descargarRemotos(sesion)
    }

    private suspend fun enviarPendientes(
        sesion: SesionUsuario
    ) {
        val pendientes = dao.obtenerPendientesDelUsuario(
            sesion.usuarioId
        )

        for (entidad in pendientes) {

            when (entidad.estadoSincronizacion) {

                "PENDIENTE_CREAR",
                "PENDIENTE_ACTUALIZAR" -> {

                    val respuesta = api.guardarRecordatorio(
                        authorization = sesion.authorizationHeader,
                        recordatorio = entidad.toDto(sesion.usuarioId)
                    )

                    if (!respuesta.isSuccessful) {
                        throw IOException(
                            "Error HTTP ${respuesta.code()} al guardar."
                        )
                    }

                    dao.confirmarSincronizacion(
                        id = entidad.id,
                        usuarioId = sesion.usuarioId,
                        version = entidad.versionLocal,
                        estadoEsperado = entidad.estadoSincronizacion
                    )
                }

                "PENDIENTE_ELIMINAR" -> {

                    val respuesta = api.eliminarRecordatorio(
                        authorization = sesion.authorizationHeader,
                        filtroId = "eq.${entidad.id}"
                    )

                    if (!respuesta.isSuccessful) {
                        throw IOException(
                            "Error HTTP ${respuesta.code()} al eliminar."
                        )
                    }

                    dao.confirmarEliminacion(
                        id = entidad.id,
                        usuarioId = sesion.usuarioId,
                        version = entidad.versionLocal
                    )
                }

                else -> {
                    throw IllegalStateException(
                        "Estado de sincronización desconocido: " +
                                entidad.estadoSincronizacion
                    )
                }
            }
        }
    }

    private suspend fun descargarRemotos(
        sesion: SesionUsuario
    ) {
        val respuesta = api.obtenerRecordatorios(
            authorization = sesion.authorizationHeader
        )

        if (!respuesta.isSuccessful) {
            throw IOException(
                "Error HTTP ${respuesta.code()} al descargar."
            )
        }

        val remotos = respuesta.body()
            ?: throw IOException(
                "Supabase devolvió una respuesta vacía."
            )

        for (remoto in remotos) {
            if (remoto.usuarioId == sesion.usuarioId) {
                dao.incorporarDesdeRemoto(
                    remoto.toLocalEntity()
                )
            }
        }
    }
}