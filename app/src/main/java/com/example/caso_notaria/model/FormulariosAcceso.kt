package com.example.caso_notaria.model

// Input de usuario en "Iniciar sesion"
data class LoginForm(
    val correo: String = "",
    val contrasena: String = ""
)

data class LoginFormError(
    val correo: String? = null,
    val contrasena: String? = null
)

// Input de usuario en "Crear cuenta"
data class RegistroForm(
    val alias: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val nivel: String = ""
)

data class RegistroFormError(
    val alias: String? = null,
    val correo: String? = null,
    val contrasena: String? = null,
    val confirmarContrasena: String? = null,
    val nivel: String? = null
)