package com.example.caso_notaria.viewmodel

import androidx.lifecycle.ViewModel
import com.example.caso_notaria.model.RegistroForm
import com.example.caso_notaria.model.RegistroFormError
import com.example.caso_notaria.model.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegistroViewModel : ViewModel() {

    private val _form = MutableStateFlow(RegistroForm())
    val form: StateFlow<RegistroForm> = _form

    private val _errores = MutableStateFlow(RegistroFormError())
    val errores: StateFlow<RegistroFormError> = _errores

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
            confirmarContrasena = if (confirmacion.isEmpty()) null // si ya escribio la confirmacion, se vuelve a revisar
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

    // Revisa todos los campos cuando apreta "Crear cuenta". Despues devuelve true si no hay errores
    fun validarTodo(): Boolean {
        val f = _form.value
        val e = RegistroFormError(
            alias = Validaciones.validarAlias(f.alias),
            correo = Validaciones.validarCorreo(f.correo),
            contrasena = Validaciones.validarContrasena(f.contrasena),
            confirmarContrasena = Validaciones.validarConfirmacion(f.contrasena, f.confirmarContrasena),
            nivel = Validaciones.validarNivel(f.nivel)
        )
        _errores.value = e
        return e.alias == null && e.correo == null && e.contrasena == null &&
                e.confirmarContrasena == null && e.nivel == null
    }
}