
package com.example.recordatoriosapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatoriosapp.data.remote.AuthRepository
import com.example.recordatoriosapp.data.remote.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.recordatoriosapp.data.remote.SesionActiva

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val sesion: LoginResponse? = null
)

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    fun actualizarEmail(valor: String) {
        _uiState.value = _uiState.value.copy(
            email = valor,
            mensajeError = null
        )
    }

    fun actualizarPassword(valor: String) {
        _uiState.value = _uiState.value.copy(
            password = valor,
            mensajeError = null
        )
    }

    fun iniciarSesion() {
        val estadoActual = _uiState.value

        if (estadoActual.cargando) return

        if (
            estadoActual.email.isBlank() ||
            estadoActual.password.isBlank()
        ) {
            _uiState.value = estadoActual.copy(
                mensajeError = "Completá el correo y la contraseña."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargando = true,
                mensajeError = null
            )

            val resultado = repository.iniciarSesion(
                email = estadoActual.email,
                password = estadoActual.password
            )

            resultado.fold(
                onSuccess = { sesion ->
                    SesionActiva.establecer(sesion)

                    _uiState.value = _uiState.value.copy(
                        password = "",
                        cargando = false,
                        sesion = sesion
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        cargando = false,
                        mensajeError = error.message
                            ?: "Ocurrió un error inesperado."
                    )
                }
            )
        }
    }
}