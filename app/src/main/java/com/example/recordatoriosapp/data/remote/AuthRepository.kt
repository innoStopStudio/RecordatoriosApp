
package com.example.recordatoriosapp.data.remote

import com.example.recordatoriosapp.BuildConfig
import kotlinx.coroutines.CancellationException

class AuthRepository {

    private val api = RetrofitClient.crearAutenticacion(
        baseUrl = BuildConfig.SUPABASE_URL,
        publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
    )

    suspend fun iniciarSesion(
        email: String,
        password: String
    ): Result<LoginResponse> {
        return try {
            val respuesta = api.iniciarSesion(
                LoginRequest(
                    email = email.trim(),
                    password = password
                )
            )

            if (respuesta.isSuccessful) {
                val sesion = respuesta.body()
                    ?: return Result.failure(
                        ErrorRed.RespuestaInvalida()
                    )

                Result.success(sesion)
            } else {
                val codigo = respuesta.code()

                val mensaje = when (codigo) {
                    400, 401 ->
                        "Credenciales incorrectas o usuario no confirmado."

                    403 ->
                        "No tenés permisos para acceder."

                    429 ->
                        "Demasiados intentos. Esperá antes de reintentar."

                    in 500..599 ->
                        "El servicio de autenticación no está disponible."

                    else ->
                        "No se pudo iniciar sesión (HTTP $codigo)."
                }

                Result.failure(
                    ErrorRed.Http(codigo, mensaje)
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.comoErrorRed())
        }
    }

    suspend fun renovarSesion(
        refreshToken: String
    ): Result<LoginResponse> {
        return try {
            val respuesta = api.renovarSesion(
                RefreshTokenRequest(refreshToken)
            )

            if (respuesta.isSuccessful) {
                val sesion = respuesta.body()
                    ?: return Result.failure(
                        ErrorRed.RespuestaInvalida()
                    )

                Result.success(sesion)
            } else {
                val codigo = respuesta.code()

                val mensaje = when (codigo) {
                    400, 401, 403 ->
                        "La sesión venció o ya no es válida. " +
                                "Volvé a iniciar sesión."

                    429 ->
                        "Demasiadas solicitudes. " +
                                "Intentá nuevamente más tarde."

                    in 500..599 ->
                        "El servidor no pudo renovar la sesión."

                    else ->
                        "No se pudo renovar la sesión (HTTP $codigo)."
                }

                Result.failure(
                    ErrorRed.Http(codigo, mensaje)
                )
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.comoErrorRed())
        }
    }

}
