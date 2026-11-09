
package com.example.recordatoriosapp.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("expires_in")
    val expiresIn: Long,

    val user: AuthUser
)

data class AuthUser(
    val id: String,
    val email: String?
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)

interface SupabaseAuthApi {

    @POST("auth/v1/token?grant_type=password")
    suspend fun iniciarSesion(
        @Body credenciales: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun renovarSesion(
        @Body solicitud: RefreshTokenRequest
    ): Response<LoginResponse>
}
