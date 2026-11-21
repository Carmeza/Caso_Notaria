package com.example.caso_notaria.repository

import com.example.caso_notaria.model.Usuario
import com.example.caso_notaria.network.ApiService
import com.example.caso_notaria.network.LoginRequest
import com.example.caso_notaria.network.RegistroRequest
import com.example.caso_notaria.network.aModelo
import retrofit2.HttpException

// Inicio de sesion, registro y sesion guardada
class UsuarioRepository(
    private val api: ApiService,
    private val sesion: SesionManager
) {

    // Devuelve el usuario si Xano acepta el correo y la contrasena.
    // Si no hay internet lanza la excepcion para que el viewmodel muestre el aviso
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        val respuesta = api.iniciarSesion(LoginRequest(correo.trim(), contrasena))
        if (respuesta.code() == 403) return null
        val token = respuesta.body()?.authToken ?: throw HttpException(respuesta)
        return guardarSesion(token)
    }

    // Crea la cuenta en Xano y deja su sesion iniciada.
    // Devuelve null si el correo ya esta registrado
    suspend fun registrar(usuario: Usuario, contrasena: String): Usuario? {
        val datos = RegistroRequest(
            name = usuario.alias,
            email = usuario.correo.trim(),
            password = contrasena,
            nivel = usuario.nivel,
            juegoPreferido = usuario.juegoPreferido,
            plataforma = usuario.plataforma
        )
        val respuesta = api.registrar(datos)
        if (respuesta.code() == 403) return null
        val token = respuesta.body()?.authToken ?: throw HttpException(respuesta)
        return guardarSesion(token)
    }

    // Pide los datos del usuario con el token nuevo y guarda los dos en el telefono
    private suspend fun guardarSesion(token: String): Usuario {
        val usuario = api.obtenerUsuarioConectado("Bearer $token").aModelo()
        sesion.guardar(token, usuario)
        return usuario
    }

    // Se lee del telefono, no necesita internet
    fun obtenerUsuarioActivo(): Usuario? = sesion.usuario()

    fun cerrarSesion() = sesion.cerrar()
}