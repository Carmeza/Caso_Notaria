package com.example.caso_notaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caso_notaria.model.ValidacionesEvento
import com.example.caso_notaria.ui.components.BotonPrincipal
import com.example.caso_notaria.ui.components.CampoTexto
import com.example.caso_notaria.ui.components.ChipCategoria
import com.example.caso_notaria.ui.components.CamaraFotoLugar
import com.example.caso_notaria.ui.theme.Caso_notariaTheme
import com.example.caso_notaria.viewmodel.PublicarEventoViewModel

private val tipos = listOf("Torneo", "LAN", "Lanzamiento", "Casual")
private val plataformas = listOf("PC", "Consola", "Mixta")
private val modalidades = listOf("Presencial", "Online")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantallaPublicarEvento(
    onEventoPublicado: () -> Unit,
    viewModel: PublicarEventoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    var mostrarCalendario by remember { mutableStateOf(false) }
    var mostrarReloj by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Publicar evento",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Los campos con * son obligatorios",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        TituloSeccion("Foto del lugar")
        CamaraFotoLugar(
            fotoUri = estado.fotoUri,
            onFotoTomada = viewModel::onFotoChange
        )

        CampoTexto(
            valor = estado.nombre,
            onCambio = viewModel::onNombreChange,
            etiqueta = "Nombre del evento *",
            error = estado.errores.nombre
        )

        // Tipo
        TituloSeccion("Tipo *")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tipos.forEach { tipo ->
                ChipCategoria(
                    texto = tipo,
                    seleccionado = estado.tipo == tipo,
                    onClick = { viewModel.onTipoChange(tipo) }
                )
            }
        }
        TextoError(estado.errores.tipo)

        CampoTexto(
            valor = estado.juego,
            onCambio = viewModel::onJuegoChange,
            etiqueta = "Juego *",
            error = estado.errores.juego,
            icono = Icons.Filled.SportsEsports
        )

        // Formato y cupos en la misma fila
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoTexto(
                valor = estado.formato,
                onCambio = viewModel::onFormatoChange,
                etiqueta = "Formato *",
                error = estado.errores.formato,
                modifier = Modifier.weight(1f)
            )
            CampoTexto(
                valor = estado.cupos,
                onCambio = viewModel::onCuposChange,
                etiqueta = "Cupos *",
                error = estado.errores.cupos,
                tipoTeclado = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }

        // Plataforma
        TituloSeccion("Plataforma *")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            plataformas.forEach { plataforma ->
                ChipCategoria(
                    texto = plataforma,
                    seleccionado = estado.plataforma == plataforma,
                    onClick = { viewModel.onPlataformaChange(plataforma) }
                )
            }
        }
        TextoError(estado.errores.plataforma)

        // Fecha y hora: se eligen con un calendario y un reloj
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoSeleccion(
                valor = estado.fechaTexto,
                etiqueta = "Fecha *",
                icono = Icons.Filled.CalendarMonth,
                error = estado.errores.fecha,
                onClick = { mostrarCalendario = true },
                modifier = Modifier.weight(1f)
            )
            CampoSeleccion(
                valor = estado.hora,
                etiqueta = "Hora *",
                icono = Icons.Filled.Schedule,
                error = estado.errores.hora,
                onClick = { mostrarReloj = true },
                modifier = Modifier.weight(1f)
            )
        }

        // Modalidad
        TituloSeccion("Modalidad *")
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            modalidades.forEachIndexed { indice, modalidad ->
                SegmentedButton(
                    selected = estado.modalidad == modalidad,
                    onClick = { viewModel.onModalidadChange(modalidad) },
                    shape = SegmentedButtonDefaults.itemShape(index = indice, count = modalidades.size),
                    icon = {
                        Icon(
                            imageVector = if (modalidad == "Presencial") Icons.Filled.LocationOn else Icons.Filled.Wifi,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                ) {
                    Text(modalidad)
                }
            }
        }

        // El lugar solo se pide si es presencial
        if (estado.modalidad == "Presencial") {
            CampoTexto(
                valor = estado.lugar,
                onCambio = viewModel::onLugarChange,
                etiqueta = "Lugar *",
                error = estado.errores.lugar,
                icono = Icons.Filled.Place
            )
        }

        CampoTexto(
            valor = estado.descripcion,
            onCambio = viewModel::onDescripcionChange,
            etiqueta = "Descripción *",
            error = estado.errores.descripcion,
            lineas = 3
        )
        Text(
            text = "${estado.descripcion.length}/${ValidacionesEvento.MAX_DESCRIPCION}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )

        BotonPrincipal(
            texto = "Publicar evento",
            cargando = estado.publicando,
            onClick = viewModel::publicarEvento
        )
    }

    // Calendario para elegir la fecha
    if (mostrarCalendario) {
        val estadoCalendario = rememberDatePickerState(initialSelectedDateMillis = estado.fechaMillis)
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoCalendario.selectedDateMillis?.let { viewModel.onFechaChange(it) }
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoCalendario)
        }
    }

    // Reloj para elegir la hora
    if (mostrarReloj) {
        val estadoReloj = rememberTimePickerState(initialHour = 18, initialMinute = 0, is24Hour = true)
        AlertDialog(
            onDismissRequest = { mostrarReloj = false },
            title = { Text("Hora del evento") },
            text = { TimePicker(state = estadoReloj) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onHoraChange(estadoReloj.hour, estadoReloj.minute)
                    mostrarReloj = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarReloj = false }) { Text("Cancelar") }
            }
        )
    }

    // Aviso cuando el evento se publico
    if (estado.publicado) {
        val cerrarAviso = {
            viewModel.limpiarFormulario()
            onEventoPublicado()
        }
        AlertDialog(
            onDismissRequest = cerrarAviso,
            title = { Text("¡Evento publicado!") },
            text = { Text("${estado.nombre} ya aparece en la lista de eventos.") },
            confirmButton = {
                TextButton(onClick = cerrarAviso) {
                    Text("Ver eventos", color = MaterialTheme.colorScheme.secondary)
                }
            }
        )
    }
}

@Composable
private fun TituloSeccion(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun TextoError(error: String?) {
    error?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

// Campo que no se escribe: al tocarlo abre el calendario o el reloj
@Composable
private fun CampoSeleccion(
    valor: String,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        )
        // Capa invisible encima para el toque
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onClick)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PantallaPublicarEventoPreview() {
    Caso_notariaTheme {
        PantallaPublicarEvento(onEventoPublicado = {})
    }
}