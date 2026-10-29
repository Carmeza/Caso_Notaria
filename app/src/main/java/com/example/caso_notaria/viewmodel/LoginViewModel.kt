package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.model.LoginForm
import com.example.caso_notaria.model.LoginFormError
import com.example.caso_notaria.model.Validaciones
import com.example.caso_notaria.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository = UsuarioRepository(AppDatabase.getDatabase(application).usuarioDao())

    private val _form = MutableStateFlow(LoginForm())
    val form: StateFlow<LoginForm> = _form.asStateFlow()

    private val _errores = MutableStateFlow(LoginFormError())
    val errores: StateFlow<LoginFormError> = _errores.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Mensaje cuando el correo o la contrasena no coinciden
    private val _errorLogin = MutableStateFlow<String?>(null)
    val errorLogin: StateFlow<String?> = _errorLogin.asStateFlow()

    fun onCorreoChange(valor: String) {
        _form.value = _form.value.copy(correo = valor)
        _errores.value = _errores.value.copy(correo = Validaciones.validarCorreo(valor))
        _errorLogin.value = null
    }

    fun onContrasenaChange(valor: String) {
        _form.value = _form.value.copy(contrasena = valor)
        _errores.value = _errores.value.copy(contrasena = Validaciones.validarContrasena(valor))
        _errorLogin.value = null
    }

    // Revisa todos los campos al apretar "Entrar". Devuelve true si no hay errores
    fun validarTodo(): Boolean {
        val formulario = _form.value
        val nuevosErrores = LoginFormError(
            correo = Validaciones.validarCorreo(formulario.correo),
            contrasena = Validaciones.validarContrasena(formulario.contrasena)
        )
        _errores.value = nuevosErrores
        return nuevosErrores.correo == null && nuevosErrores.contrasena == null
    }

    // Busca el usuario en Room y deja su sesion activa. Solo llama a onExito si los datos son correctos
    fun iniciarSesion(onExito: () -> Unit) {
        if (!validarTodo()) return
        viewModelScope.launch {
            _cargando.value = true
            delay(800) // espera breve para mostrar la carga
            val usuario = usuarioRepository.iniciarSesion(_form.value.correo, _form.value.contrasena)
            _cargando.value = false
            if (usuario == null) {
                _errorLogin.value = "Correo o contraseña incorrectos"
            } else {
                onExito()
            }
        }
    }
}