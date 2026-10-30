package com.example.caso_notaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caso_notaria.viewmodel.ChatDetalleViewModel
import com.example.caso_notaria.viewmodel.MensajeUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaChatDetalle(
    chatId: Int,
    usuarioId: Int,
    onVolver: () -> Unit,
    viewModel: ChatDetalleViewModel = viewModel()
) {
    // Carga los mensajes e información del chat según el ID del torneo y usuario activo
    LaunchedEffect(chatId, usuarioId) {
        viewModel.cargarMensajes(chatId, usuarioId)
    }

    val mensajes by viewModel.mensajes.collectAsState()
    val tituloChat by viewModel.tituloChat.collectAsState() // Recibe el nombre real del torneo

    var textoMensaje by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        // Muestra dinámicamente el nombre del torneo (ej: "Torneo Regional Smash" o "Minecraft")
                        Text(tituloChat, style = MaterialTheme.typography.titleMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BarraEscribirMensaje(
                texto = textoMensaje,
                onCambio = { textoMensaje = it },
                onEnviar = {
                    viewModel.enviarMensaje(textoMensaje)
                    textoMensaje = ""
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                    Text("Hoy", style = MaterialTheme.typography.labelSmall, modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape).padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }

            items(mensajes) { mensaje ->
                BurbujaMensaje(mensaje)
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun BurbujaMensaje(mensaje: MensajeUi) {
    val alineacion = if (mensaje.esMio) Alignment.End else Alignment.Start
    val colorFondo = if (mensaje.esMio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val colorTexto = if (mensaje.esMio) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alineacion
    ) {
        if (!mensaje.esMio) {
            Text(text = mensaje.emisor, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 48.dp, bottom = 2.dp))
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (mensaje.esMio) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!mensaje.esMio) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(mensaje.iniciales, color = MaterialTheme.colorScheme.onSecondary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
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
                Column {
                    Text(text = mensaje.texto, color = colorTexto, style = MaterialTheme.typography.bodyMedium)
                    Text(text = mensaje.hora, color = colorTexto.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun BarraEscribirMensaje(texto: String, onCambio: (String) -> Unit, onEnviar: () -> Unit) {
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
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onEnviar,
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}