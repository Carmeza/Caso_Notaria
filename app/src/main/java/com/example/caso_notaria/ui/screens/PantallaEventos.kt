package com.example.caso_notaria.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caso_notaria.ui.components.AvatarIniciales
import com.example.caso_notaria.ui.components.CampoTexto
import com.example.caso_notaria.ui.components.ChipCategoria
import com.example.caso_notaria.ui.components.TarjetaEvento
import com.example.caso_notaria.viewmodel.EventosViewModel

private val filtros = listOf("Todos", "Torneo", "LAN", "Lanzamiento", "Casual")

@Composable
fun PantallaEventos(
    onEventoClick: (Int) -> Unit,
    viewModel: EventosViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    // Recarga al volver a la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarEventos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Saludo y avatar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                if (estado.aliasUsuario.isNotBlank()) {
                    Text(
                        text = "Hola, ${estado.aliasUsuario}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Eventos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            if (estado.aliasUsuario.isNotBlank()) {
                AvatarIniciales(nombre = estado.aliasUsuario, tamano = 44.dp)
            }
        }

        CampoTexto(
            valor = estado.busqueda,
            onCambio = viewModel::onBusquedaChange,
            etiqueta = "Buscar evento o juego",
            icono = Icons.Filled.Search
        )

        // Filtros por tipo
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtros) { filtro ->
                ChipCategoria(
                    texto = filtro,
                    seleccionado = estado.tipoSeleccionado == filtro,
                    onClick = { viewModel.onTipoChange(filtro) }
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Próximos eventos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (estado.eventosFiltrados.size == 1) "1 evento" else "${estado.eventosFiltrados.size} eventos",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        when {
            estado.cargando -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().weight(1f)) {
                    CircularProgressIndicator()
                }
            }
            estado.eventosFiltrados.isEmpty() -> {
                Text(
                    text = "No hay eventos que coincidan con tu búsqueda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                )
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(estado.eventosFiltrados, key = { it.evento.id }) { item ->
                        TarjetaEvento(
                            evento = item.evento,
                            inscritos = item.inscritos,
                            onClick = { onEventoClick(item.evento.id) },
                            // Las tarjetas se acomodan con animacion al filtrar
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}