package com.example.caso_notaria.repository

import com.example.caso_notaria.model.Usuario
import com.example.caso_notaria.model.UsuarioDao

// Acceso a los usuarios y a la sesion. Usar de los viewmodels nunca del dao directo
class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    // Devuelve el usuario si el correo y la contrasena son correctos, o null si no.
    // Deja la sesion activa solo para ese usuario
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        val usuario = usuarioDao.iniciarSesion(correo.trim(), contrasena) ?: return null
        usuarioDao.cerrarTodasLasSesiones()
        usuarioDao.activarSesion(usuario.id)
        return usuario.copy(sesionActiva = true)
    }

    // Guarda la cuenta nueva y deja su sesion iniciada.
    // Devuelve null si el correo ya esta registrado
    suspend fun registrar(usuario: Usuario): Usuario? {
        if (usuarioDao.buscarPorCorreo(usuario.correo.trim()) != null) return null
        usuarioDao.cerrarTodasLasSesiones()
        val nuevo = usuario.copy(correo = usuario.correo.trim(), sesionActiva = true)
        val id = usuarioDao.insert(nuevo)
        return nuevo.copy(id = id.toInt())
    }

    suspend fun obtenerUsuarioActivo(): Usuario? = usuarioDao.obtenerUsuarioActivo()

    suspend fun obtenerUsuario(id: Int): Usuario? = usuarioDao.getById(id)

    suspend fun cerrarSesion() = usuarioDao.cerrarTodasLasSesiones()
}