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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caso_notaria.ui.components.BotonPrincipal
import com.example.caso_notaria.ui.components.CampoTexto
import com.example.caso_notaria.ui.theme.Caso_notariaTheme
import com.example.caso_notaria.viewmodel.RegistroViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PantallaCrearCuenta(
    onCuentaCreada: (Int) -> Unit,
    onIrAIniciarSesion: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val context = LocalContext.current // Obtenemos el contexto para Room
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                text = "Regístrate para comenzar a participar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            CampoTexto(
                valor = form.alias,
                onCambio = viewModel::onAliasChange,
                etiqueta = "Alias / Nombre",
                error = errores.alias
            )

            CampoTexto(
                valor = form.correo,
                onCambio = viewModel::onCorreoChange,
                etiqueta = "Correo electrónico",
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
                etiqueta = "Confirmar contraseña",
                error = errores.confirmarContrasena,
                esContrasena = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            BotonPrincipal(
                texto = "Registrarse",
                habilitado = !cargando,
                onClick = {
                    if (viewModel.validarTodo()) {
                        scope.launch {
                            cargando = true
                            delay(1000)
                            // Pasamos 'context' como argumento al ViewModel
                            val nuevoUsuarioId = viewModel.registrarUsuario(context)
                            cargando = false
                            onCuentaCreada(nuevoUsuarioId)
                        }
                    }
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

@Preview(showBackground = true)
@Composable
private fun PantallaCrearCuentaPreview() {
    Caso_notariaTheme {
        PantallaCrearCuenta(
            onCuentaCreada = {},
            onIrAIniciarSesion = {},
            onVolver = {}
        )
    }
}