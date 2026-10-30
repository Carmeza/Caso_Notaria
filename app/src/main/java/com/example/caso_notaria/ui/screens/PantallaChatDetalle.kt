package com.example.caso_notaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caso_notaria.ui.components.AvatarIniciales
import com.example.caso_notaria.viewmodel.ChatDetalleViewModel
import com.example.caso_notaria.viewmodel.MensajeUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaChatDetalle(
    eventoId: Int,
    onVolver: () -> Unit,
    viewModel: ChatDetalleViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val lista = rememberLazyListState()

    LaunchedEffect(eventoId) {
        viewModel.cargar(eventoId)
    }

    // Baja al ultimo mensaje cuando cambia la conversacion
    LaunchedEffect(estado.mensajes.size) {
        if (estado.mensajes.isNotEmpty()) lista.animateScrollToItem(estado.mensajes.size - 1)
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(estado.titulo, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            BarraEscribirMensaje(
                texto = estado.texto,
                onCambio = viewModel::onTextoChange,
                onEnviar = viewModel::enviar
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            state = lista,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(estado.mensajes, key = { it.id }) { mensaje ->
                if (mensaje.esSistema) AvisoSistema(mensaje.texto) else BurbujaMensaje(mensaje)
            }
        }
    }
}

// Aviso centrado, por ejemplo "kuma_07 se unio al grupo"
@Composable
private fun AvisoSistema(texto: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun BurbujaMensaje(mensaje: MensajeUi) {
    val colorFondo = if (mensaje.esMio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val colorTexto = if (mensaje.esMio) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (mensaje.esMio) Alignment.End else Alignment.Start
    ) {
        if (!mensaje.esMio) {
            Text(
                text = mensaje.emisor,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 44.dp, bottom = 2.dp)
            )
        }

        Row(verticalAlignment = Alignment.Bottom) {
            if (!mensaje.esMio) {
                AvatarIniciales(nombre = mensaje.emisor, tamano = 36.dp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (mensaje.esMio) 16.dp else 4.dp,
                            bottomEnd = if (mensaje.esMio) 4.dp else 16.dp
                        )
                    )
                    .background(colorFondo)
                    .padding(12.dp)
            ) {
                Text(text = mensaje.texto, color = colorTexto, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = mensaje.hora,
                    color = colorTexto.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun BarraEscribirMensaje(texto: String, onCambio: (String) -> Unit, onEnviar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = texto,
            onValueChange = onCambio,
            placeholder = { Text("Escribe un mensaje...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onEnviar,
            enabled = texto.isNotBlank(),
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}