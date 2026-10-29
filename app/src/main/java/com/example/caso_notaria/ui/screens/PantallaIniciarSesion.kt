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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caso_notaria.ui.components.BotonPrincipal
import com.example.caso_notaria.ui.components.CampoTexto
import com.example.caso_notaria.ui.theme.Caso_notariaTheme
import com.example.caso_notaria.viewmodel.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PantallaIniciarSesion(
    onLoginExitoso: (Int) -> Unit, // Recibe el ID del usuario consultado en Room
    onIrACrearCuenta: () -> Unit,
    onVolver: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Ingresa tus datos para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Correo conectado directamente al ViewModel
            CampoTexto(
                valor = form.correo,
                onCambio = viewModel::onCorreoChange,
                etiqueta = "Correo",
                error = errores.correo,
                tipoTeclado = KeyboardType.Email
            )

            // Campo Contraseña conectado directamente al ViewModel
            CampoTexto(
                valor = form.contrasena,
                onCambio = viewModel::onContrasenaChange,
                etiqueta = "Contraseña",
                error = errores.contrasena,
                esContrasena = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de Entrar
            BotonPrincipal(
                texto = "Entrar",
                habilitado = !cargando,
                onClick = {
                    if (viewModel.validarTodo()) {
                        scope.launch {
                            cargando = true
                            delay(1200) // Simulación de verificación
                            val usuarioId = viewModel.obtenerUsuarioIdPorCorreo()
                            cargando = false
                            onLoginExitoso(usuarioId)
                        }
                    }
                }
            )

            TextButton(
                onClick = onIrACrearCuenta,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Overlay con pantalla de carga centrada
        AnimatedVisibility(
            visible = cargando,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PantallaIniciarSesionPreview() {
    Caso_notariaTheme {
        PantallaIniciarSesion(
            onLoginExitoso = {},
            onIrACrearCuenta = {},
            onVolver = {}
        )
    }
}