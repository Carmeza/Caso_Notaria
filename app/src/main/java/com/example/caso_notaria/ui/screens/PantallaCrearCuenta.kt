package com.example.caso_notaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.caso_notaria.ui.components.ChipCategoria
import com.example.caso_notaria.ui.theme.Caso_notariaTheme
import com.example.caso_notaria.viewmodel.RegistroViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private val niveles = listOf("Casual", "Intermedio", "Competitivo")

@Composable
fun PantallaCrearCuenta(
    onCuentaCreada: () -> Unit,
    onIrAIniciarSesion: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    var mostrarModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(onClick = onVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Completa tus datos para empezar",
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

        // Nivel
        Text(
            text = "Nivel",
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
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        BotonPrincipal(
            texto = "Crear cuenta",
            onClick = {
                if (viewModel.validarTodo()) {
                    // despues se guarda el usuario en la base de datos
                    mostrarModal = true
                }
            }
        )

        TextButton(
            onClick = onIrAIniciarSesion,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿Ya tienes cuenta? Iniciar sesion", color = MaterialTheme.colorScheme.secondary)
        }
        //Modal confirmacion cuenta creada
        if (mostrarModal) {
            val cerrarModal = {
                mostrarModal = false
                onCuentaCreada()
            }
            AlertDialog(
                onDismissRequest = cerrarModal,
                title = { Text("Cuenta creada!") },
                text = { Text("Hola, ${form.alias}. Tu cuenta se creó correctamente.") },
                confirmButton = {
                    TextButton(onClick = cerrarModal) {
                        Text("Continuar", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            )
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