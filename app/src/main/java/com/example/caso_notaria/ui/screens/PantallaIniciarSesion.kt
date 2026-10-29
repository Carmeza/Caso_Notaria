package com.example.caso_notaria.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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
import com.example.caso_notaria.viewmodel.LoginFormState
import com.example.caso_notaria.viewmodel.LoginErroresState
import com.example.caso_notaria.viewmodel.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PantallaIniciarSesion(
    onLoginExitoso: (Int) -> Unit,
    onIrACrearCuenta: () -> Unit,
    onVolver: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val scope = rememberCoroutineScope()

    PantallaIniciarSesionContent(
        form = form,
        errores = errores,
        onCorreoChange = viewModel::onCorreoChange,
        onContrasenaChange = viewModel::onContrasenaChange,
        onValidarYEntrar = {
            if (viewModel.validarTodo()) {
                scope.launch {
                    val usuarioId = viewModel.obtenerUsuarioIdPorCorreo()
                    onLoginExitoso(usuarioId)
                }
            }
        },
        onIrACrearCuenta = onIrACrearCuenta,
        onVolver = onVolver
    )
}

@Composable
fun PantallaIniciarSesionContent(
    form: LoginFormState,
    errores: LoginErroresState,
    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onValidarYEntrar: () -> Unit,
    onIrACrearCuenta: () -> Unit,
    onVolver: () -> Unit
) {
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onVolver, enabled = !cargando) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Iniciar sesion",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Ingresa con tu correo y contraseña",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            CampoTexto(
                valor = form.correo,
                onCambio = onCorreoChange,
                etiqueta = "Correo",
                error = errores.correo,
                tipoTeclado = KeyboardType.Email
            )
            CampoTexto(
                valor = form.contrasena,
                onCambio = onContrasenaChange,
                etiqueta = "Contraseña",
                error = errores.contrasena,
                esContrasena = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            BotonPrincipal(
                texto = "Entrar",
                habilitado = !cargando,
                onClick = {
                    scope.launch {
                        cargando = true
                        delay(1200)
                        cargando = false
                        onValidarYEntrar()
                    }
                }
            )

            TextButton(
                onClick = onIrACrearCuenta,
                enabled = !cargando,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("¿No tienes cuenta? Crear cuenta", color = MaterialTheme.colorScheme.secondary)
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
fun PantallaIniciarSesionPreview() {
    Caso_notariaTheme {
        PantallaIniciarSesionContent(
            form = LoginFormState(correo = "kuma07@correo.cl"),
            errores = LoginErroresState(),
            onCorreoChange = {},
            onContrasenaChange = {},
            onValidarYEntrar = {},
            onIrACrearCuenta = {},
            onVolver = {}
        )
    }
}