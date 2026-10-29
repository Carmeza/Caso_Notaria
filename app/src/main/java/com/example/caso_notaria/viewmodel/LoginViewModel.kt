package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.caso_notaria.model.AppDatabase
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
        _errores.value = _errores.value.copy(correo = null)
    }

    fun onContrasenaChange(contrasena: String) {
        _form.value = _form.value.copy(contrasena = contrasena)
        _errores.value = _errores.value.copy(contrasena = null)
    }

    fun validarTodo(): Boolean {
        var valido = true
        var errCorreo: String? = null
        var errContrasena: String? = null

        if (_form.value.correo.isBlank()) {
            errCorreo = "El correo es requerido"
            valido = false
        }
        if (_form.value.contrasena.isBlank()) {
            errContrasena = "La contraseña es requerida"
            valido = false
        }

        _errores.value = LoginErroresState(correo = errCorreo, contrasena = errContrasena)
        return valido
    }

    suspend fun obtenerUsuarioIdPorCorreo(): Int {
        val correoIngresado = _form.value.correo.trim()
        val usuarioEncontrado = usuarioDao.buscarPorCorreo(correoIngresado)
        return usuarioEncontrado?.id ?: 1
    }
}