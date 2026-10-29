package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.model.LoginForm
import com.example.caso_notaria.model.LoginFormError
import com.example.caso_notaria.model.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val usuarioDao = db.usuarioDao()

    private val _form = MutableStateFlow(LoginForm())
    val form: StateFlow<LoginForm> = _form.asStateFlow()

    private val _errores = MutableStateFlow(LoginFormError())
    val errores: StateFlow<LoginFormError> = _errores.asStateFlow()

    fun onCorreoChange(valor: String) {
        _form.value = _form.value.copy(correo = valor)
        _errores.value = _errores.value.copy(correo = Validaciones.validarCorreo(valor))
    }

    fun onContrasenaChange(valor: String) {
        _form.value = _form.value.copy(contrasena = valor)
        _errores.value = _errores.value.copy(contrasena = Validaciones.validarContrasena(valor))
    }

    fun validarTodo(): Boolean {
        val formulario = _form.value
        val nuevosErrores = LoginFormError(
            correo = Validaciones.validarCorreo(formulario.correo),
            contrasena = Validaciones.validarContrasena(formulario.contrasena)
        )
        _errores.value = nuevosErrores
        return nuevosErrores.correo == null && nuevosErrores.contrasena == null
    }

    // Busca en Room el ID real según el correo ingresado
    suspend fun obtenerUsuarioIdPorCorreo(): Int {
        val correoIngresado = _form.value.correo.trim()
        val usuarioEncontrado = usuarioDao.buscarPorCorreo(correoIngresado)
        return usuarioEncontrado?.id ?: -1
    }
}