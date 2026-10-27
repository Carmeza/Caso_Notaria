package com.example.caso_notaria.viewmodel

import androidx.lifecycle.ViewModel
import com.example.caso_notaria.model.LoginForm
import com.example.caso_notaria.model.LoginFormError
import com.example.caso_notaria.model.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _form = MutableStateFlow(LoginForm())
    val form: StateFlow<LoginForm> = _form

    private val _errores = MutableStateFlow(LoginFormError())
    val errores: StateFlow<LoginFormError> = _errores

    fun onCorreoChange(valor: String) {
        _form.value = _form.value.copy(correo = valor)
        _errores.value = _errores.value.copy(correo = Validaciones.validarCorreo(valor))
    }

    fun onContrasenaChange(valor: String) {
        _form.value = _form.value.copy(contrasena = valor)
        _errores.value = _errores.value.copy(contrasena = Validaciones.validarContrasena(valor))
    }

    // Revisa todos los campos al apretar "Entrar". Despues devuelve true si no hay errores
    fun validarTodo(): Boolean {
        val f = _form.value
        val e = LoginFormError(
            correo = Validaciones.validarCorreo(f.correo),
            contrasena = Validaciones.validarContrasena(f.contrasena)
        )
        _errores.value = e
        return e.correo == null && e.contrasena == null
    }
}