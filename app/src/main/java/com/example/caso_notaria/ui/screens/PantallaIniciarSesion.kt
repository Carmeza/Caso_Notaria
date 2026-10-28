package com.example.caso_notaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caso_notaria.ui.components.BotonPrincipal
import com.example.caso_notaria.ui.components.CampoTexto
import com.example.caso_notaria.ui.theme.Caso_notariaTheme
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(onClick = onVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }

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

        Spacer(modifier = Modifier.height(8.dp))

        BotonPrincipal(
            texto = "Entrar",
            onClick = {
                if (viewModel.validarTodo()) {
                    // despues se  revisa el correo y contraseña en la base de datos
                    onLoginExitoso()
                }
            }
        )

        TextButton(
            onClick = onIrACrearCuenta,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿No tienes cuenta? Crear cuenta", color = MaterialTheme.colorScheme.secondary)
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