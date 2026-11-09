
package com.example.recordatoriosapp.data

import android.util.Log
import com.example.recordatoriosapp.data.local.RecordatorioDao
import com.example.recordatoriosapp.data.mapper.toDomain
import com.example.recordatoriosapp.data.mapper.toEntity
import com.example.recordatoriosapp.data.remote.SesionActiva
import com.example.recordatoriosapp.model.Recordatorio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.example.recordatoriosapp.data.remote.AuthRepository
import com.example.recordatoriosapp.data.remote.ErrorRed
import kotlinx.coroutines.delay
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecordatorioRepositoryRoom(
    private val dao: RecordatorioDao
) : RecordatorioRepository {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private val sincronizador = SincronizadorRecordatorios(dao)

    private val mutexSincronizacion = Mutex()

    private val authRepository = AuthRepository()

    private val _estadoSincronizacion =
        MutableStateFlow<EstadoSincronizacion>(
            EstadoSincronizacion.SinComprobar
        )

    val estadoSincronizacion = _estadoSincronizacion.asStateFlow()

    init {
        scope.launch {
            SesionActiva.sesion.collect { sesion ->
                if (sesion != null) {
                    solicitarSincronizacion()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observarRecordatorios(): Flow<List<Recordatorio>> {
        return SesionActiva.sesion.flatMapLatest { sesion ->
            if (sesion == null) {
                flowOf(emptyList())
            } else {
                dao.observarDelUsuario(sesion.usuarioId)
                    .map { entidades ->
                        entidades.map { entidad ->
                            entidad.toDomain()
                        }
                    }
            }
        }
    }

    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        return observarRecordatorios().first()
    }

    override suspend fun guardarRecordatorio(
        recordatorio: Recordatorio
    ) {
        val sesion = SesionActiva.sesion.value
            ?: throw IllegalStateException(
                "Es necesario iniciar sesión."
            )

        dao.insertar(
            recordatorio.toEntity(
                pendienteSincronizacion = true,
                usuarioId = sesion.usuarioId
            ).copy(
                estadoSincronizacion = "PENDIENTE_CREAR",
                versionLocal = 1L,
                eliminado = false
            )
        )

        solicitarSincronizacion()
    }

    override suspend fun actualizarRecordatorio(
        recordatorio: Recordatorio
    ) {
        val sesion = SesionActiva.sesion.value
            ?: throw IllegalStateException(
                "Es necesario iniciar sesión."
            )

        val entidad = recordatorio.toEntity(
            usuarioId = sesion.usuarioId
        )

        val modificados = dao.actualizarLocal(
            id = entidad.id,
            usuarioId = sesion.usuarioId,
            titulo = entidad.titulo,
            descripcion = entidad.descripcion,
            categoria = entidad.categoria,
            prioridad = entidad.prioridad,
            estado = entidad.estado,
            fechaAviso = entidad.fechaAviso,
            ubicacion = entidad.ubicacion
        )

        if (modificados == 0) {
            throw IllegalArgumentException(
                "El recordatorio no existe o no pertenece al usuario."
            )
        }

        solicitarSincronizacion()
    }

    override suspend fun eliminarRecordatorio(id: String) {
        val sesion = SesionActiva.sesion.value
            ?: throw IllegalStateException(
                "Es necesario iniciar sesión."
            )

        val modificados = dao.marcarEliminado(
            id = id,
            usuarioId = sesion.usuarioId
        )

        if (modificados == 0) {
            throw IllegalArgumentException(
                "El recordatorio no existe o no pertenece al usuario."
            )
        }

        solicitarSincronizacion()
    }

    fun solicitarSincronizacion() {
        scope.launch {
            mutexSincronizacion.withLock {
                if (SesionActiva.sesion.value == null) {
                    _estadoSincronizacion.value =
                        EstadoSincronizacion.SinComprobar
                    return@withLock
                }

                _estadoSincronizacion.value =
                    EstadoSincronizacion.Sincronizando

                val maxIntentos = 3

                for (intento in 1..maxIntentos) {
                    try {
                        ejecutarSincronizacionConSesionValida()

                        // Solo informamos que terminó el intento.
                        // La renovación puede haber fallado sin
                        // lanzar una excepción.
                        _estadoSincronizacion.value =
                            EstadoSincronizacion.Pendiente(
                                "Sincronización finalizada. " +
                                        "Los cambios locales están protegidos."
                            )
                        return@withLock

                    } catch (e: CancellationException) {
                        throw e

                    } catch (e: ErrorRed.Http) {
                        val temporal =
                            e.codigo == 429 ||
                                    e.codigo in 500..599

                        if (!temporal || intento == maxIntentos) {
                            _estadoSincronizacion.value =
                                EstadoSincronizacion.Error(
                                    "Error HTTP ${e.codigo}. " +
                                            "Los cambios siguen guardados."
                                )
                            return@withLock
                        }

                    } catch (e: IOException) {
                        if (intento == maxIntentos) {
                            _estadoSincronizacion.value =
                                EstadoSincronizacion.Error(
                                    "No se pudo conectar. " +
                                            "Los cambios siguen guardados."
                                )
                            return@withLock
                        }

                    } catch (e: Exception) {
                        _estadoSincronizacion.value =
                            EstadoSincronizacion.Error(
                                "No se pudo completar la sincronización."
                            )
                        return@withLock
                    }

                    delay(2000L * intento)
                }
            }
        }
    }


    private suspend fun ejecutarSincronizacionConSesionValida() {
        var sesion = SesionActiva.sesion.value ?: return

        if (SesionActiva.estaPorVencer()) {
            val renovada = intentarRenovarSesion() ?: return
            sesion = renovada
        }

        try {
            sincronizador.sincronizar(sesion)
        } catch (e: ErrorRed.Http) {
            if (e.codigo != 401) throw e

            // Si Supabase rechazó el token, intentamos renovarlo
            // una sola vez antes de repetir la sincronización.
            val renovada = intentarRenovarSesion() ?: return

            sincronizador.sincronizar(renovada)
        }
    }

    private suspend fun intentarRenovarSesion():
            com.example.recordatoriosapp.data.remote.SesionUsuario? {

        val token = SesionActiva.obtenerRefreshToken()
            ?: return null

        val resultado = authRepository.renovarSesion(token)

        return resultado.fold(
            onSuccess = { respuesta ->
                SesionActiva.establecer(respuesta)
                SesionActiva.sesion.value
            },
            onFailure = { error ->
                Log.w(
                    "RecordatoriosSync",
                    "No se pudo renovar la sesión: ${error.message}"
                )

                // No cerramos la sesión ante errores temporales.
                // Solo invalidamos una renovación rechazada
                // expresamente por el servidor.
                if (
                    error is ErrorRed.Http &&
                    error.codigo in listOf(400, 401, 403)
                ) {
                    SesionActiva.cerrar()
                }

                null
            }
        )
    }

}