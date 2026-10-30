package com.example.caso_notaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caso_notaria.ui.components.AvatarIniciales
import com.example.caso_notaria.ui.components.BotonPrincipal
import com.example.caso_notaria.ui.components.BotonSecundario
import com.example.caso_notaria.ui.components.Etiqueta
import com.example.caso_notaria.ui.components.iconoTipo
import com.example.caso_notaria.ui.theme.TurquesaOscuro
import com.example.caso_notaria.ui.theme.VioletaOscuro
import com.example.caso_notaria.viewmodel.DetalleEventoViewModel
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantallaDetalleEvento(
    eventoId: Int,
    onVolver: () -> Unit,
    onVerParticipantes: () -> Unit,
    onIrAlChat: () -> Unit,
    viewModel: DetalleEventoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(eventoId) {
        viewModel.cargar(eventoId)
    }

    val evento = estado.evento
    if (evento == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (estado.cargando) CircularProgressIndicator() else Text(estado.mensaje ?: "")
        }
        return
    }
    //foto del lugar, si se tomo al publicar el evento. Igual que en CamaraFotoLugar
    val context = LocalContext.current
    val foto = remember(evento.fotoUri) {
        evento.fotoUri?.let { MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(it)) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            BarraInscripcion(
                cuposLibres = estado.cuposLibres,
                cupos = evento.cupos,
                textoBoton = when {
                    estado.esOrganizador -> "Eres el organizador"
                    estado.estaInscrito -> "Ya estás inscrito"
                    estado.cuposLibres <= 0 -> "Sin cupos"
                    else -> "Inscribirme"
                },
                habilitado = !estado.esOrganizador && !estado.estaInscrito && estado.cuposLibres > 0,
                cargando = estado.inscribiendo,
                mensaje = estado.mensaje,
                onInscribirse = viewModel::inscribirse
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Cabecera: foto del lugar, o degradado con el icono del tipo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(Brush.linearGradient(listOf(VioletaOscuro, TurquesaOscuro)))
            ) {
                if (foto != null) {
                    Image(
                        bitmap = foto.asImageBitmap(),
                        contentDescription = "Foto del lugar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Etiqueta(
                        texto = "Foto del lugar",
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    )
                } else {
                    Icon(
                        imageVector = iconoTipo(evento.tipo),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Etiqueta(evento.tipo, colorTexto = MaterialTheme.colorScheme.secondary)
                    Etiqueta(evento.juego)
                    Etiqueta(evento.formato)
                    Etiqueta(evento.plataforma)
                }

                Text(
                    text = evento.nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarIniciales(nombre = estado.organizador, tamano = 28.dp)
                    Text(
                        text = "  Organiza ${estado.organizador}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Datos principales
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        FilaDato(Icons.Filled.CalendarMonth, evento.fecha, "${evento.hora} hrs")
                        if (evento.modalidad == "Online") {
                            FilaDato(Icons.Filled.Wifi, "Online", "El link se comparte en el chat del evento")
                        } else {
                            FilaDato(Icons.Filled.LocationOn, evento.lugar, "Presencial")
                        }
                        FilaDato(Icons.Filled.SportsEsports, "${evento.juego} · ${evento.formato}", evento.plataforma)
                    }
                }

                Text(
                    text = "Sobre el evento",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = evento.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quienes van, lleva a la lista de participantes
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onVerParticipantes)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                            estado.participantes.take(4).forEach { participante ->
                                AvatarIniciales(
                                    nombre = participante.alias,
                                    tamano = 32.dp,
                                    modifier = Modifier.border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(
                                text = if (estado.inscritos == 1) "1 persona va" else "${estado.inscritos} personas van",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                textDecoration = TextDecoration.Underline
                            )
                            Text(
                                text = "Ver participantes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }

    // Confirmacion despues de inscribirse
    if (estado.mostrarConfirmacion) {
        ModalBottomSheet(
            onDismissRequest = viewModel::cerrarConfirmacion,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "¡Ya estás inscrito!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Te uniste al chat grupal del evento. Coordina con los otros ${estado.inscritos - 1} jugadores cómo y a qué hora llegar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Etiqueta("${evento.fecha} · ${evento.hora} · ${if (evento.modalidad == "Online") "Online" else evento.lugar}")
                Spacer(modifier = Modifier.height(4.dp))
                BotonPrincipal(texto = "Ir al chat del evento", onClick = {
                    viewModel.cerrarConfirmacion()
                    onIrAlChat()
                })
                BotonSecundario(texto = "Seguir viendo el evento", onClick = viewModel::cerrarConfirmacion)
            }
        }
    }
}

// Barra fija de abajo con los cupos y el boton para inscribirse
@Composable
private fun BarraInscripcion(
    cuposLibres: Int,
    cupos: Int,
    textoBoton: String,
    habilitado: Boolean,
    cargando: Boolean,
    mensaje: String?,
    onInscribirse: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.padding(end = 16.dp)) {
                    Text(
                        text = "${cuposLibres.coerceAtLeast(0)} cupos libres",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "de $cupos · gratis",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BotonPrincipal(
                    texto = textoBoton,
                    onClick = onInscribirse,
                    habilitado = habilitado,
                    cargando = cargando,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FilaDato(icono: ImageVector, titulo: String, detalle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Text(detalle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}