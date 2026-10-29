package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.model.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginFormState(
    val correo: String = "",
    val contrasena: String = ""
)

data class LoginErroresState(
    val correo: String? = null,
    val contrasena: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val usuarioDao = db.usuarioDao()

    private val _form = MutableStateFlow(LoginFormState())
    val form: StateFlow<LoginFormState> = _form.asStateFlow()

    private val _errores = MutableStateFlow(LoginErroresState())
    val errores: StateFlow<LoginErroresState> = _errores.asStateFlow()

    fun onCorreoChange(correo: String) {
        _form.value = _form.value.copy(correo = correo)
        _errores.value = _errores.value.copy(correo = Validaciones.validarCorreo(correo))
    }

    fun onContrasenaChange(contrasena: String) {
        _form.value = _form.value.copy(contrasena = contrasena)
        _errores.value = _errores.value.copy(contrasena = Validaciones.validarContrasena(contrasena))
    }

    // Valida todos los campos y retorna true si no hay errores
    fun validarTodo(): Boolean {
        val formulario = _form.value
        val errCorreo = if (formulario.correo.isBlank()) {
            "El correo es requerido"
        } else {
            Validaciones.validarCorreo(formulario.correo)
        }

        val errContrasena = if (formulario.contrasena.isBlank()) {
            "La contraseña es requerida"
        } else {
            Validaciones.validarContrasena(formulario.contrasena)
        }

        val nuevosErrores = LoginErroresState(
            correo = errCorreo,
            contrasena = errContrasena
        )
        _errores.value = nuevosErrores

        return nuevosErrores.correo == null && nuevosErrores.contrasena == null
    }

    // Consulta en Room el ID del usuario que inicia sesión
    suspend fun obtenerUsuarioIdPorCorreo(): Int {
        val correoIngresado = _form.value.correo.trim()
        val usuarioEncontrado = usuarioDao.buscarPorCorreo(correoIngresado)
        return usuarioEncontrado?.id ?: 1
    }
}