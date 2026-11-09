
package com.example.recordatoriosapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class ConectividadObserver(
    context: Context,
    private val alRecuperarInternet: () -> Unit
) {

    private val connectivityManager =
        context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

    private var observando = false

    private val callback =
        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                // Esperamos a que Android valide la red.
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                if (
                    capabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                    ) &&
                    capabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                ) {
                    alRecuperarInternet()
                }
            }
        }

    fun iniciar() {
        if (observando) return

        val solicitud = NetworkRequest.Builder()
            .addCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
            .build()

        connectivityManager.registerNetworkCallback(
            solicitud,
            callback
        )

        observando = true
    }

    fun detener() {
        if (!observando) return

        connectivityManager.unregisterNetworkCallback(callback)
        observando = false
    }

    fun hayInternetValidado(): Boolean {
        val red = connectivityManager.activeNetwork
            ?: return false

        val capacidades =
            connectivityManager.getNetworkCapabilities(red)
                ?: return false

        return capacidades.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        ) && capacidades.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        )
    }
}
