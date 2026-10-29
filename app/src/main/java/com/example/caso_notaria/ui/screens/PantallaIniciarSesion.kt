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
import com.example.caso_notaria.viewmodel.LoginViewModel

@Composable
fun PantallaIniciarSesion(
    onLoginExitoso: () -> Unit,
    onIrACrearCuenta: () -> Unit,
    onVolver: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val errorLogin by viewModel.errorLogin.collectAsState()

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
                text = "Hola de nuevo",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Inicia sesión para ver tus eventos y los chats de tus torneos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            // Error cuando el correo o la contrasena no coinciden con una cuenta
            errorLogin?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            BotonPrincipal(
                texto = "Iniciar sesión",
                habilitado = !cargando,
                onClick = { viewModel.iniciarSesion(onExito = onLoginExitoso) }
            )

            TextButton(
                onClick = onIrACrearCuenta,
                enabled = !cargando,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("¿No tienes cuenta? Crear cuenta", color = MaterialTheme.colorScheme.secondary)
            }
        }

        // Capa de carga mientras se revisa la cuenta
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