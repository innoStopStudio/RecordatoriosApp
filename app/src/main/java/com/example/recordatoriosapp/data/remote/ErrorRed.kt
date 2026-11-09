
package com.example.recordatoriosapp.data.remote

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class ErrorRed(
    message: String,
    cause: Throwable? = null
) : IOException(message, cause) {

    // No se pudo establecer conexión con el servidor.
    class SinConexion(cause: Throwable? = null) :
        ErrorRed(
            "No hay conexión con el servidor. Revisá Internet.",
            cause
        )

    // La solicitud superó el tiempo de espera.
    class TiempoAgotado(cause: Throwable? = null) :
        ErrorRed(
            "La conexión tardó demasiado. Intentá nuevamente.",
            cause
        )

    // El servidor respondió con un código HTTP de error.
    class Http(
        val codigo: Int,
        mensaje: String
    ) : ErrorRed(mensaje)

    // El servidor respondió sin los datos esperados.
    class RespuestaInvalida :
        ErrorRed(
            "El servidor devolvió una respuesta inválida."
        )
}

// Convierte excepciones de red en errores reconocibles
// por las distintas capas de la aplicación.
fun Throwable.comoErrorRed(): IOException {
    return when (this) {

        // Conserva los errores que ya fueron clasificados.
        is ErrorRed -> this

        // La conexión superó el tiempo permitido.
        is SocketTimeoutException ->
            ErrorRed.TiempoAgotado(this)

        // No se pudo resolver el nombre del servidor.
        is UnknownHostException ->
            ErrorRed.SinConexion(this)

        // Otros errores de entrada/salida o conectividad.
        is IOException ->
            ErrorRed.SinConexion(this)

        // Errores inesperados que no son de red.
        else ->
            IOException(
                "Ocurrió un error inesperado.",
                this
            )
    }
}
