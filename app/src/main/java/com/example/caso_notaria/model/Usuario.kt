package com.example.caso_notaria.model

// Usuario. La contrasena no se guarda en la app, solo se envia a Xano al entrar o registrarse
data class Usuario(
    val id: Int = 0,
    val alias: String,
    val correo: String,
    val nivel: String,
    val juegoPreferido: String = "",
    val plataforma: String = ""
)