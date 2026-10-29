package com.example.caso_notaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.caso_notaria.model.AppDatabase
import com.example.caso_notaria.model.RegistroForm
import com.example.caso_notaria.model.RegistroFormError
import com.example.caso_notaria.model.Usuario
import com.example.caso_notaria.model.Validaciones
import com.example.caso_notaria.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository = UsuarioRepository(AppDatabase.getDatabase(application).usuarioDao())

    private val _form = MutableStateFlow(RegistroForm())
    val form: StateFlow<RegistroForm> = _form.asStateFlow()

    private val _errores = MutableStateFlow(RegistroFormError())
    val errores: StateFlow<RegistroFormError> = _errores.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    fun onAliasChange(valor: String) {
        _form.value = _form.value.copy(alias = valor)
        _errores.value = _errores.value.copy(alias = Validaciones.validarAlias(valor))
    }

    fun onCorreoChange(valor: String) {
        _form.value = _form.value.copy(correo = valor)
        _errores.value = _errores.value.copy(correo = Validaciones.validarCorreo(valor))
    }

    fun onContrasenaChange(valor: String) {
        _form.value = _form.value.copy(contrasena = valor)
        val confirmacion = _form.value.confirmarContrasena
        _errores.value = _errores.value.copy(
            contrasena = Validaciones.validarContrasena(valor),
            confirmarContrasena = if (confirmacion.isEmpty()) null
            else Validaciones.validarConfirmacion(valor, confirmacion)
        )
    }

    fun onConfirmarContrasenaChange(valor: String) {
        _form.value = _form.value.copy(confirmarContrasena = valor)
        _errores.value = _errores.value.copy(
            confirmarContrasena = Validaciones.validarConfirmacion(_form.value.contrasena, valor)
        )
    }

    fun onNivelChange(valor: String) {
        _form.value = _form.value.copy(nivel = valor)
        _errores.value = _errores.value.copy(nivel = null)
    }

    // Revisa todos los campos cuando apreta "Crear cuenta". Devuelve true si no hay errores
    fun validarTodo(): Boolean {
        val formulario = _form.value
        val nuevosErrores = RegistroFormError(
            alias = Validaciones.validarAlias(formulario.alias),
            correo = Validaciones.validarCorreo(formulario.correo),
            contrasena = Validaciones.validarContrasena(formulario.contrasena),
            confirmarContrasena = Validaciones.validarConfirmacion(formulario.contrasena, formulario.confirmarContrasena),
            nivel = Validaciones.validarNivel(formulario.nivel)
        )
        _errores.value = nuevosErrores
        return nuevosErrores.alias == null && nuevosErrores.correo == null && nuevosErrores.contrasena == null &&
                nuevosErrores.confirmarContrasena == null && nuevosErrores.nivel == null
    }

    // Guarda la cuenta en Room con la sesion iniciada. Si el correo ya existe, lo marca como error
    fun registrar(onExito: () -> Unit) {
        if (!validarTodo()) return
        val formulario = _form.value
        viewModelScope.launch {
            _cargando.value = true
            delay(800) // espera breve para mostrar la carga
            val nuevo = usuarioRepository.registrar(
                Usuario(
                    alias = formulario.alias.trim(),
                    correo = formulario.correo,
                    contrasena = formulario.contrasena,
                    nivel = formulario.nivel
                )
            )
            _cargando.value = false
            if (nuevo == null) {
                _errores.value = _errores.value.copy(correo = "Este correo ya está registrado")
            } else {
                onExito()
            }
        }
    }
}