
package com.example.recordatoriosapp.data.remote

data class SesionUsuario(
    val usuarioId: String,
    val accessToken: String
) {
    val authorizationHeader: String
        get() = "Bearer $accessToken"
}