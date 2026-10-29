package com.example.caso_notaria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
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
import com.example.caso_notaria.model.RegistroForm
import com.example.caso_notaria.model.RegistroFormError
import com.example.caso_notaria.ui.components.BotonPrincipal
import com.example.caso_notaria.ui.components.CampoTexto
import com.example.caso_notaria.ui.components.ChipCategoria
import com.example.caso_notaria.ui.theme.Caso_notariaTheme
import com.example.caso_notaria.viewmodel.RegistroViewModel
import kotlinx.coroutines.launch

private val niveles = listOf("Casual", "Intermedio", "Competitivo")

@Composable
fun PantallaCrearCuenta(
    onCuentaCreada: (Int) -> Unit, // Recibe el ID del nuevo usuario
    onIrAIniciarSesion: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    var mostrarModal by remember { mutableStateOf(false) }
    var nuevoUsuarioId by remember { mutableIntStateOf(-1) }
    val scope = rememberCoroutineScope()

    PantallaCrearCuentaContent(
        form = form,
        errores = errores,
        onAliasChange = viewModel::onAliasChange,
        onCorreoChange = viewModel::onCorreoChange,
        onContrasenaChange = viewModel::onContrasenaChange,
        onConfirmarContrasenaChange = viewModel::onConfirmarContrasenaChange,
        onNivelChange = viewModel::onNivelChange,
        onCrearCuentaClick = {
            if (viewModel.validarTodo()) {
                scope.launch {
                    // Guarda el usuario en Room en segundo plano
                    nuevoUsuarioId = viewModel.registrarUsuario()
                    mostrarModal = true
                }
            }
        },
        onIrAIniciarSesion = onIrAIniciarSesion,
        onVolver = onVolver,
        mostrarModal = mostrarModal,
        onDismissModal = {
            mostrarModal = false
            onCuentaCreada(nuevoUsuarioId)
        }
    )
}

@Composable
fun PantallaCrearCuentaContent(
    form: RegistroForm,
    errores: RegistroFormError,
    onAliasChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onConfirmarContrasenaChange: (String) -> Unit,
    onNivelChange: (String) -> Unit,
    onCrearCuentaClick: () -> Unit,
    onIrAIniciarSesion: () -> Unit,
    onVolver: () -> Unit,
    mostrarModal: Boolean,
    onDismissModal: () -> Unit
) {
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
            onCambio = onAliasChange,
            etiqueta = "Alias",
            error = errores.alias
        )
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
        CampoTexto(
            valor = form.confirmarContrasena,
            onCambio = onConfirmarContrasenaChange,
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
                    onClick = { onNivelChange(nivel) }
                )
            }
        }
        errores.nivel?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        BotonPrincipal(
            texto = "Crear cuenta",
            onClick = onCrearCuentaClick
        )

        TextButton(
            onClick = onIrAIniciarSesion,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿Ya tienes cuenta? Iniciar sesion", color = MaterialTheme.colorScheme.secondary)
        }

        // Modal confirmación cuenta creada
        if (mostrarModal) {
            AlertDialog(
                onDismissRequest = onDismissModal,
                title = { Text("¡Cuenta creada!") },
                text = { Text("Hola, ${form.alias}. Tu cuenta se creó correctamente.") },
                confirmButton = {
                    TextButton(onClick = onDismissModal) {
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
        PantallaCrearCuentaContent(
            form = RegistroForm(alias = "UsuarioPrueba", correo = "prueba@correo.cl", nivel = "Intermedio"),
            errores = RegistroFormError(),
            onAliasChange = {},
            onCorreoChange = {},
            onContrasenaChange = {},
            onConfirmarContrasenaChange = {},
            onNivelChange = {},
            onCrearCuentaClick = {},
            onIrAIniciarSesion = {},
            onVolver = {},
            mostrarModal = false,
            onDismissModal = {}
        )
    }
}