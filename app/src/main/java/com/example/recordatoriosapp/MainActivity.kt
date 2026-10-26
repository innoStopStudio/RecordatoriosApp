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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecordatoriosAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRecordatorios(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaRecordatorios(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Patagonia Servicios Técnicos - " +
                        "${SimulacionDeDatos.recordatorios.size} recordatorios"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(
            items = SimulacionDeDatos.recordatorios,
            key = { it.id }
        ) { recordatorio ->
            TarjetaRecordatorio(recordatorio)

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TarjetaRecordatorio(recordatorio: Recordatorio) {
    val codigoColor = SimulacionDeDatos.coloresCategorias[
        recordatorio.categoria.id
    ] ?: "#667085"

    val colorCategoria = Color(
        AndroidColor.parseColor(codigoColor)
    )
    Card(
        modifier = Modifier.fillMaxWidth()
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
        PantallaRecordatorios()
    }
}