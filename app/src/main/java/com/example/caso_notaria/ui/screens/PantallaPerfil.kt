package com.example.caso_notaria.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caso_notaria.ui.components.AvatarIniciales
import com.example.caso_notaria.ui.components.Etiqueta
import com.example.caso_notaria.viewmodel.EventoInscritoUi
import com.example.caso_notaria.viewmodel.PerfilViewModel
import com.example.caso_notaria.viewmodel.ChatsViewModel

@Composable
fun PantallaPerfil(
    onCerrarSesion: () -> Unit,
    viewModel: PerfilViewModel = viewModel(),
    chatsViewModel: ChatsViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    // Recarga al volver a la pestana, por si se inscribio en algo nuevo
    LaunchedEffect(Unit) {
        viewModel.cargarPerfil()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Tarjeta con avatar, nivel y numeros
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarIniciales(nombre = estado.alias, tamano = 80.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = estado.alias,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (estado.nivel.isNotBlank()) {
                    Etiqueta(estado.nivel, colorTexto = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ItemEstadistica(estado.eventosInscritos.size, "Eventos\ninscritos")
                    ItemEstadistica(estado.eventosOrganizados, "Eventos\norganizados")
                    ItemEstadistica(estado.chats, "Chats\nactivos")
                }
            }
        }

        Text("Datos básicos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilaDatoBasico(Icons.Filled.Email, "Correo", estado.correo)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                FilaDatoBasico(Icons.Filled.SportsEsports, "Juego preferido", estado.juegoPreferido)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                FilaDatoBasico(Icons.Filled.Computer, "Plataforma", estado.plataforma)
            }
        }

        Text("Eventos inscritos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (estado.eventosInscritos.isEmpty() && !estado.cargando) {
            Text(
                text = "Todavía no te inscribes en ningún evento.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        estado.eventosInscritos.forEach { evento ->
            ItemEventoInscrito(evento)
        }

        // Cerrar sesion: primero pide confirmacion
        OutlinedButton(
            onClick = viewModel::pedirCerrarSesion,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar sesión", style = MaterialTheme.typography.labelLarge)
        }
    }

    // Modal de confirmacion para cerrar sesion
    if (estado.mostrarConfirmacionCierre) {
        AlertDialog(
            onDismissRequest = viewModel::cancelarCerrarSesion,
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Vas a volver a la bienvenida. Tus inscripciones y chats se mantienen.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cerrarSesion(onListo = {
                        chatsViewModel.limpiarChats()
                        onCerrarSesion()
                    })
                }) {
                    Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelarCerrarSesion) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.secondary)
                }
            }
        )
    }
}

@Composable
private fun ItemEstadistica(numero: Int, etiqueta: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = numero.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FilaDatoBasico(icono: ImageVector, titulo: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp).weight(1f)
        )
        Text(text = valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ItemEventoInscrito(evento: EventoInscritoUi) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Fecha en un recuadro: dia arriba y mes abajo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(evento.dia, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(evento.mes, style = MaterialTheme.typography.labelMedium)
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(evento.titulo, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(evento.detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}