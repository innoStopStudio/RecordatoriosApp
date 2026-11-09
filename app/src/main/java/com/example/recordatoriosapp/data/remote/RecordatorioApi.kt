
package com.example.recordatoriosapp.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface RecordatorioApi {

    @GET("rest/v1/recordatorios")
    suspend fun obtenerRecordatorios(
        @Header("Authorization") authorization: String,
        @Query("select") columnas: String = "*"
    ): Response<List<RecordatorioDto>>

    @Headers(
        "Prefer: resolution=merge-duplicates,return=minimal"
    )
    @POST("rest/v1/recordatorios")
    suspend fun guardarRecordatorio(
        @Header("Authorization") authorization: String,
        @Body recordatorio: RecordatorioDto
    ): Response<Unit>

    @DELETE("rest/v1/recordatorios")
    suspend fun eliminarRecordatorio(
        @Header("Authorization") authorization: String,
        @Query("id") filtroId: String
    ): Response<Unit>
}