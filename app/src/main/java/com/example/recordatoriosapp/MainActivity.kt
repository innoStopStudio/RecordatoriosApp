package com.example.recordatoriosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.recordatoriosapp.ui.theme.RecordatoriosAppTheme
import com.example.recordatoriosapp.model.SimulacionDeDatos
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import com.example.recordatoriosapp.model.Recordatorio
import com.example.recordatoriosapp.model.Prioridad
import com.example.recordatoriosapp.model.EstadoRecordatorio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor
import com.example.recordatoriosapp.data.RecordatorioRepositoryRoom
import com.example.recordatoriosapp.data.local.RecordatoriosDatabase
import androidx.activity.viewModels
import com.example.recordatoriosapp.presentation.ListadoViewModel
import com.example.recordatoriosapp.presentation.ListadoViewModelFactory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import com.example.recordatoriosapp.presentation.ListadoUiState
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import com.example.recordatoriosapp.navigation.NavegacionApp
import androidx.compose.foundation.clickable
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.recordatoriosapp.data.preferences.PreferenciasRepository
import com.example.recordatoriosapp.presentation.PreferenciasViewModel
import com.example.recordatoriosapp.presentation.PreferenciasViewModelFactory
import android.os.Handler
import android.os.Looper

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        val database = RecordatoriosDatabase.obtenerInstancia(
            applicationContext
        )

        RecordatorioRepositoryRoom(database.recordatorioDao())
    }

    private val preferenciasRepository by lazy {
        PreferenciasRepository(applicationContext)
    }

    private val preferenciasViewModel: PreferenciasViewModel by viewModels {
        PreferenciasViewModelFactory(preferenciasRepository)
    }

    private val listadoViewModel: ListadoViewModel by viewModels {
        ListadoViewModelFactory(repository)
    }

    private lateinit var conectividadObserver: ConectividadObserver

    private val handlerPrincipal = Handler(Looper.getMainLooper())

    private val solicitarSyncPorConectividad = Runnable {
        repository.solicitarSincronizacion()
    }

    override fun onStart() {
        super.onStart()

        if (!::conectividadObserver.isInitialized) {
            conectividadObserver = ConectividadObserver(
                applicationContext
            ) {
                programarSincronizacion()
            }
        }

        conectividadObserver.iniciar()

        // También comprobamos la conexión al regresar
        // a la aplicación desde segundo plano.
        if (conectividadObserver.hayInternetValidado()) {
            programarSincronizacion()
        }
    }

    private fun programarSincronizacion() {
        handlerPrincipal.removeCallbacks(
            solicitarSyncPorConectividad
        )

        // Agrupamos notificaciones consecutivas de red.
        handlerPrincipal.postDelayed(
            solicitarSyncPorConectividad,
            1500L
        )
    }

    override fun onStop() {
        handlerPrincipal.removeCallbacks(
            solicitarSyncPorConectividad
        )

        if (::conectividadObserver.isInitialized) {
            conectividadObserver.detener()
        }

        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val preferencias by preferenciasViewModel.preferencias
                .collectAsStateWithLifecycle()

            RecordatoriosAppTheme(
                darkTheme = preferencias.temaOscuro
            ) {
                NavegacionApp(
                    listadoViewModel = listadoViewModel,
                    repository = repository,
                    preferenciasViewModel = preferenciasViewModel
                )
            }
        }
    }
}

@Composable
fun PantallaRecordatorios(
    recordatorios: List<Recordatorio>,
    modifier: Modifier = Modifier,
    onCrear: () -> Unit = {},
    onVerDetalle: (String) -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Patagonia Servicios Técnicos - " +
                            "${recordatorios.size} recordatorios"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(
                items = recordatorios,
                key = { it.id }
            ) { recordatorio ->
                TarjetaRecordatorio(
                    recordatorio = recordatorio,
                    onClick = { onVerDetalle(recordatorio.id) }
                )
            }
        }

        FloatingActionButton(
            onClick = onCrear,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
fun TarjetaRecordatorio(
    recordatorio: Recordatorio,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val codigoColor = SimulacionDeDatos.coloresCategorias[
        recordatorio.categoria.id
    ] ?: "#667085"

    val colorCategoria = Color(
        AndroidColor.parseColor(codigoColor)
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = recordatorio.titulo)

            Text(
                text = "Categoría: ${recordatorio.categoria.nombre}",
                color = colorCategoria
            )

            Text(
                text = "Prioridad: ${when (recordatorio.prioridad) {
                    Prioridad.Alta -> "Alta"
                    Prioridad.Media -> "Media"
                    Prioridad.Baja -> "Baja"
                }}"
            )

            Text(
                text = "Estado: ${when (recordatorio.estado) {
                    EstadoRecordatorio.Pendiente -> "Pendiente"
                    EstadoRecordatorio.Completado -> "Completado"
                    EstadoRecordatorio.Cancelado -> "Cancelado"
                }}"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaRecordatoriosPreview() {
    RecordatoriosAppTheme {
        PantallaRecordatorios(
            recordatorios = SimulacionDeDatos.recordatorios
        )
    }
}