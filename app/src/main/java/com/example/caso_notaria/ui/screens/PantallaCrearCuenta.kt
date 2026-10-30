package com.example.caso_notaria.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caso_notaria.ui.components.BotonPrincipal
import com.example.caso_notaria.ui.components.CampoTexto
import com.example.caso_notaria.ui.components.ChipCategoria
import com.example.caso_notaria.viewmodel.RegistroViewModel
import com.example.caso_notaria.viewmodel.ChatsViewModel

private val niveles = listOf("Casual", "Intermedio", "Competitivo")

@Composable
fun PantallaCrearCuenta(
    onCuentaCreada: () -> Unit,
    onIrAIniciarSesion: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel(),
    chatsViewModel: ChatsViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onVolver, enabled = !cargando) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Solo lo necesario para encontrar compañía. Tu correo no se muestra a otros jugadores.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            CampoTexto(
                valor = form.alias,
                onCambio = viewModel::onAliasChange,
                etiqueta = "Alias",
                error = errores.alias
            )

            CampoTexto(
                valor = form.correo,
                onCambio = viewModel::onCorreoChange,
                etiqueta = "Correo",
                error = errores.correo,
                tipoTeclado = KeyboardType.Email
            )

            CampoTexto(
                valor = form.contrasena,
                onCambio = viewModel::onContrasenaChange,
                etiqueta = "Contraseña",
                error = errores.contrasena,
                esContrasena = true
            )

            CampoTexto(
                valor = form.confirmarContrasena,
                onCambio = viewModel::onConfirmarContrasenaChange,
                etiqueta = "Repetir contraseña",
                error = errores.confirmarContrasena,
                esContrasena = true
            )

            // Nivel de juego
            Text(
                text = "Nivel de juego",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                niveles.forEach { nivel ->
                    ChipCategoria(
                        texto = nivel,
                        seleccionado = form.nivel == nivel,
                        onClick = { viewModel.onNivelChange(nivel) }
                    )
                }
            }
            errores.nivel?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            BotonPrincipal(
                texto = "Crear cuenta",
                habilitado = !cargando,
                onClick = {
                    viewModel.registrar(onExito = {
                        chatsViewModel.limpiarChats()
                        onCuentaCreada()
                    })
                }
            )

            TextButton(
                onClick = onIrAIniciarSesion,
                enabled = !cargando,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = MaterialTheme.colorScheme.secondary)
            }
        }

        // Capa de carga mientras se guarda la cuenta
        AnimatedVisibility(
            visible = cargando,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 5.dp
                )
            }
        }
    }
}