package com.example.caso_notaria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.caso_notaria.model.Evento

// Icono segun el tipo de evento. Tambien se usa en el detalle
fun iconoTipo(tipo: String): ImageVector = when (tipo) {
    "Torneo" -> Icons.Filled.EmojiEvents
    "LAN" -> Icons.Filled.Computer
    "Lanzamiento" -> Icons.Filled.NewReleases
    else -> Icons.Filled.SportsEsports
}

@Composable
fun TarjetaEvento(
    evento: Evento,
    inscritos: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icono del tipo de evento
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.tertiary, MaterialTheme.shapes.small)
            ) {
                Icon(iconoTipo(evento.tipo), contentDescription = evento.tipo, tint = MaterialTheme.colorScheme.onBackground)
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                        Etiqueta(
                            texto = evento.tipo,
                            colorTexto = MaterialTheme.colorScheme.secondary
                        )
                        Etiqueta(texto = evento.juego, modifier = Modifier.weight(1f, fill = false))
                    }
                    // Inscritos sobre el total de cupos
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Groups,
                            contentDescription = "Inscritos",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " $inscritos/${evento.cupos}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = evento.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                DatoConIcono(Icons.Filled.CalendarMonth, "${evento.fecha} · ${evento.hora}")
                DatoConIcono(
                    icono = if (evento.modalidad == "Online") Icons.Filled.Wifi else Icons.Filled.LocationOn,
                    texto = if (evento.modalidad == "Online") "Online" else evento.lugar
                )
            }
        }
    }
}

@Composable
private fun DatoConIcono(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Text(
            text = "  $texto",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}