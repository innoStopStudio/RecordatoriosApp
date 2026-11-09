
package com.example.recordatoriosapp.data.remote

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SesionActiva {

    private val _sesion = MutableStateFlow<SesionUsuario?>(null)
    val sesion = _sesion.asStateFlow()

    private var refreshToken: String? = null
    private var vencimientoMillis: Long = 0L

    @Synchronized
    fun establecer(respuesta: LoginResponse) {
        _sesion.value = SesionUsuario(
            usuarioId = respuesta.user.id,
            accessToken = respuesta.accessToken
        )

        refreshToken = respuesta.refreshToken

        vencimientoMillis =
            System.currentTimeMillis() +
                    respuesta.expiresIn * 1000L
    }

    @Synchronized
    fun obtenerRefreshToken(): String? = refreshToken

    @Synchronized
    fun estaPorVencer(): Boolean {
        if (_sesion.value == null) return false

        // Margen de seguridad de un minuto.
        return System.currentTimeMillis() >=
                vencimientoMillis - 60_000L
    }

    @Synchronized
    fun cerrar() {
        _sesion.value = null
        refreshToken = null
        vencimientoMillis = 0L
    }
}
