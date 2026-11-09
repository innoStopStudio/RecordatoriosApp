
package com.example.recordatoriosapp.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recordatoriosapp.PantallaRecordatorios
import com.example.recordatoriosapp.data.RecordatorioRepository
import com.example.recordatoriosapp.data.preferences.OrdenRecordatorios
import com.example.recordatoriosapp.model.EstadoRecordatorio
import com.example.recordatoriosapp.model.Prioridad
import com.example.recordatoriosapp.presentation.CreacionViewModel
import com.example.recordatoriosapp.presentation.CreacionViewModelFactory
import com.example.recordatoriosapp.presentation.EstadoListado
import com.example.recordatoriosapp.presentation.ListadoUiState
import com.example.recordatoriosapp.presentation.ListadoViewModel
import com.example.recordatoriosapp.presentation.LoginViewModel
import com.example.recordatoriosapp.presentation.PantallaCrear
import com.example.recordatoriosapp.presentation.PantallaDetalle
import com.example.recordatoriosapp.presentation.PantallaLogin
import com.example.recordatoriosapp.presentation.PantallaPreferencias
import com.example.recordatoriosapp.presentation.PreferenciasViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.launch
import com.example.recordatoriosapp.presentation.PantallaEditar
import androidx.compose.foundation.layout.Column
import com.example.recordatoriosapp.data.RecordatorioRepositoryRoom
import com.example.recordatoriosapp.data.EstadoSincronizacion
import com.example.recordatoriosapp.presentation.IndicadorSincronizacion

@Composable
fun NavegacionApp(
    listadoViewModel: ListadoViewModel,
    repository: RecordatorioRepository,
    preferenciasViewModel: PreferenciasViewModel
) {
    val navController = rememberNavController()

    val preferencias by preferenciasViewModel.preferencias
        .collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {

        // Inicio de sesión
        composable(Rutas.LOGIN) {
            val loginViewModel: LoginViewModel = viewModel()

            PantallaLogin(
                viewModel = loginViewModel,
                onLoginExitoso = {
                    navController.navigate(Rutas.LISTADO) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Listado de recordatorios
        composable(Rutas.LISTADO) {
            val uiState by listadoViewModel.uiState
                .collectAsStateWithLifecycle()

            val repositorioRoom =
                repository as? RecordatorioRepositoryRoom

            val estadoSync by (
                    repositorioRoom?.estadoSincronizacion
                        ?: kotlinx.coroutines.flow.MutableStateFlow(
                            EstadoSincronizacion.SinComprobar
                        )
                    ).collectAsStateWithLifecycle()

            Scaffold(
                topBar = {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    navController.navigate(Rutas.PREFERENCIAS)
                                }
                            ) {
                                Text("Preferencias")
                            }
                        }

                        IndicadorSincronizacion(
                            estado = estadoSync,
                            onReintentar = {
                                repositorioRoom?.solicitarSincronizacion()
                            }
                        )
                    }
                }
            ) { innerPadding ->

                when (val estado = uiState) {

                    is ListadoUiState.Cargando -> {
                        EstadoListado(
                            mensaje = "Cargando recordatorios...",
                            cargando = true,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    is ListadoUiState.ConDatos -> {
                        val visibles = estado.recordatorios.filter { recordatorio ->
                            preferencias.mostrarCompletados ||
                                    recordatorio.estado != EstadoRecordatorio.Completado
                        }

                        val ordenados = when (preferencias.orden) {
                            OrdenRecordatorios.TITULO ->
                                visibles.sortedBy {
                                    it.titulo.lowercase()
                                }

                            OrdenRecordatorios.PRIORIDAD ->
                                visibles.sortedWith(
                                    compareByDescending<
                                            com.example.recordatoriosapp.model.Recordatorio
                                            > {
                                        when (it.prioridad) {
                                            Prioridad.Alta -> 3
                                            Prioridad.Media -> 2
                                            Prioridad.Baja -> 1
                                        }
                                    }.thenBy {
                                        it.titulo.lowercase()
                                    }
                                )
                        }

                        PantallaRecordatorios(
                            recordatorios = ordenados,
                            modifier = Modifier.padding(innerPadding),
                            onCrear = {
                                navController.navigate(Rutas.CREAR)
                            },
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

        // Crear recordatorio
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
                    navController.popBackStack()
                }
            )
        }


        // Detalle de recordatorio
        composable(Rutas.DETALLE) { entrada ->
            val id = entrada.arguments?.getString("id")

            val estado by listadoViewModel.uiState
                .collectAsStateWithLifecycle()

            val recordatorio = (estado as? ListadoUiState.ConDatos)
                ?.recordatorios
                ?.find { it.id == id }

            val scope = rememberCoroutineScope()
            var eliminando by remember { mutableStateOf(false) }
            var errorEliminacion by remember {
                mutableStateOf<String?>(null)
            }

            PantallaDetalle(
                recordatorio = recordatorio,
                onVolver = {
                    navController.popBackStack()
                },
                onEditar = {
                    if (id != null) {
                        navController.navigate("editar/$id")
                    }
                },
                onEliminar = {
                    if (!eliminando && id != null) {
                        eliminando = true
                        scope.launch {
                            try {
                                repository.eliminarRecordatorio(id)
                                navController.popBackStack()
                            } catch (e: Exception) {
                                errorEliminacion =
                                    "No se pudo eliminar el recordatorio."
                            } finally {
                                eliminando = false
                            }
                        }
                    }
                }
            )

            if (errorEliminacion != null) {
                AlertDialog(
                    onDismissRequest = {
                        errorEliminacion = null
                    },
                    title = { Text("Error") },
                    text = {
                        Text(errorEliminacion.orEmpty())
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                errorEliminacion = null
                            }
                        ) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }

        // Editar recordatorio
        composable("editar/{id}") { entrada ->
            val id = entrada.arguments?.getString("id")

            val estado by listadoViewModel.uiState
                .collectAsStateWithLifecycle()

            val recordatorio = (estado as? ListadoUiState.ConDatos)
                ?.recordatorios
                ?.find { it.id == id }

            PantallaEditar(
                recordatorio = recordatorio,
                repository = repository,
                onVolver = {
                    navController.popBackStack()
                },
                onGuardado = {
                    navController.popBackStack()
                }
            )
        }

        // Preferencias
        composable(Rutas.PREFERENCIAS) {
            PantallaPreferencias(
                viewModel = preferenciasViewModel,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}