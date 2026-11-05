package com.example.recordatoriosapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recordatoriosapp.PantallaRecordatorios
import com.example.recordatoriosapp.presentation.ListadoUiState
import com.example.recordatoriosapp.presentation.ListadoViewModel
import com.example.recordatoriosapp.data.RecordatorioRepository
import com.example.recordatoriosapp.presentation.PantallaCrear
import com.example.recordatoriosapp.presentation.PantallaDetalle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatoriosapp.presentation.CreacionViewModel
import com.example.recordatoriosapp.presentation.CreacionViewModelFactory
import com.example.recordatoriosapp.presentation.EstadoListado

@Composable
fun NavegacionApp(
    listadoViewModel: ListadoViewModel,
    repository: RecordatorioRepository
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LISTADO
    ) {
        composable(Rutas.LISTADO) {

            val uiState by listadoViewModel.uiState.collectAsStateWithLifecycle()

            Scaffold { innerPadding ->
                when (val estado = uiState) {

                    is ListadoUiState.Cargando -> {
                        EstadoListado(
                            mensaje = "Cargando recordatorios...",
                            cargando = true,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    is ListadoUiState.ConDatos -> {
                        PantallaRecordatorios(
                            recordatorios = estado.recordatorios,
                            modifier = Modifier.padding(innerPadding),
                            onCrear = { navController.navigate(Rutas.CREAR) },
                            onVerDetalle = { id ->
                                navController.navigate(Rutas.detalle(id))
                            }
                        )
                    }

                    is ListadoUiState.Vacio -> {
                        EstadoListado(
                            mensaje = "Todavía no hay recordatorios.",
                            textoBoton = "Crear recordatorio",
                            onAccion = {
                                navController.navigate(Rutas.CREAR)
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    is ListadoUiState.Error -> {
                        EstadoListado(
                            mensaje = estado.mensaje,
                            textoBoton = "Reintentar",
                            onAccion = {
                                listadoViewModel.cargarRecordatorios()
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }

        composable(Rutas.CREAR) {

            val creacionViewModel: CreacionViewModel = viewModel(
                factory = CreacionViewModelFactory(repository)
            )

            PantallaCrear(
                viewModel = creacionViewModel,
                onVolver = {
                    navController.popBackStack()
                },
                onGuardado = {
                    listadoViewModel.cargarRecordatorios()
                    navController.popBackStack()
                }
            )
        }

        composable(Rutas.DETALLE) { entrada ->
            val id = entrada.arguments?.getString("id")

            val estado by listadoViewModel.uiState.collectAsStateWithLifecycle()

            val recordatorio = (estado as? ListadoUiState.ConDatos)
                ?.recordatorios
                ?.find { it.id == id }

            PantallaDetalle(
                recordatorio = recordatorio,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}