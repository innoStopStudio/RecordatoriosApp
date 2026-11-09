
package com.example.recordatoriosapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "preferencias_usuario"
)

class PreferenciasRepository(
    context: Context
) {
    private val dataStore = context.applicationContext.dataStore

    private object Claves {
        val TEMA_OSCURO = booleanPreferencesKey("tema_oscuro")
        val ORDEN = stringPreferencesKey("orden_recordatorios")
        val MOSTRAR_COMPLETADOS = booleanPreferencesKey("mostrar_completados")
    }

    val preferencias: Flow<PreferenciasUsuario> =
        dataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(androidx.datastore.preferences.core.emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { valores ->
                val ordenGuardado = valores[Claves.ORDEN]

                val orden = OrdenRecordatorios.entries
                    .firstOrNull { it.name == ordenGuardado }
                    ?: OrdenRecordatorios.TITULO

                PreferenciasUsuario(
                    temaOscuro = valores[Claves.TEMA_OSCURO] ?: false,
                    orden = orden,
                    mostrarCompletados =
                        valores[Claves.MOSTRAR_COMPLETADOS] ?: true
                )
            }

    suspend fun cambiarTemaOscuro(activado: Boolean) {
        dataStore.edit { valores ->
            valores[Claves.TEMA_OSCURO] = activado
        }
    }

    suspend fun cambiarOrden(orden: OrdenRecordatorios) {
        dataStore.edit { valores ->
            valores[Claves.ORDEN] = orden.name
        }
    }

    suspend fun cambiarMostrarCompletados(mostrar: Boolean) {
        dataStore.edit { valores ->
            valores[Claves.MOSTRAR_COMPLETADOS] = mostrar
        }
    }
}