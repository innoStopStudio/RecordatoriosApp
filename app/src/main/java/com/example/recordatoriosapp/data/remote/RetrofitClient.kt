
package com.example.recordatoriosapp.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private fun crearRetrofit(
        baseUrl: String,
        publishableKey: String
    ): Retrofit {

        require(baseUrl.startsWith("https://")) {
            "Supabase debe utilizar HTTPS"
        }

        require(baseUrl.endsWith("/")) {
            "La URL base debe terminar con /"
        }

        val clienteHttp = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val solicitud = chain.request()
                    .newBuilder()
                    .header("apikey", publishableKey)
                    .build()

                chain.proceed(solicitud)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun crearAutenticacion(
        baseUrl: String,
        publishableKey: String
    ): SupabaseAuthApi {
        return crearRetrofit(
            baseUrl,
            publishableKey
        ).create(SupabaseAuthApi::class.java)
    }

    fun crearRecordatorios(
        baseUrl: String,
        publishableKey: String
    ): RecordatorioApi {
        return crearRetrofit(
            baseUrl,
            publishableKey
        ).create(RecordatorioApi::class.java)
    }
}
