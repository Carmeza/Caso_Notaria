package com.example.caso_notaria.model

// Reglas de validacion.
// Cada funcion devuelve el mensaje de error, o null si esta todo bien
object Validaciones {

    fun validarAlias(alias: String): String? = when {
        alias.isBlank() -> "El alias es obligatorio"
        alias.length < 3 -> "Mínimo 3 caracteres"
        alias.contains(" ") -> "El alias no puede tener espacios"
        else -> null
    }

    fun validarCorreo(correo: String): String? = when {
        correo.isBlank() -> "El correo es obligatorio"
        !correo.matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$")) -> "Ingresa un correo válido, por ejemplo nombre@correo.cl"
        else -> null
    }

    fun validarContrasena(contrasena: String): String? = when {
        contrasena.isBlank() -> "La contraseña es obligatoria"
        contrasena.length < 8 -> "Debe tener al menos 8 caracteres"
        else -> null
    }

    fun validarConfirmacion(contrasena: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Repite la contraseña"
        confirmacion != contrasena -> "Las contraseñas no coinciden"
        else -> null
    }

    fun validarNivel(nivel: String): String? =
        if (nivel.isBlank()) "Elige tu nivel" else null
}