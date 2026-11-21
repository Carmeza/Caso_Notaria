package com.example.caso_notaria.repository

import android.content.Context
import androidx.core.content.edit
import com.example.caso_notaria.model.Usuario

// Guarda en el telefono el token de Xano y los datos del usuario conectado.
// SharedPreferences no se borra al cerrar la app
class SesionManager(context: Context) {

    private val preferencias = context.applicationContext.getSharedPreferences("sesion_mygos", Context.MODE_PRIVATE)

    fun token(): String? = preferencias.getString(TOKEN, null)

    fun guardar(token: String, usuario: Usuario) {
        preferencias.edit {
            putString(TOKEN, token)
            putInt(ID, usuario.id)
            putString(ALIAS, usuario.alias)
            putString(CORREO, usuario.correo)
            putString(NIVEL, usuario.nivel)
            putString(JUEGO, usuario.juegoPreferido)
            putString(PLATAFORMA, usuario.plataforma)
        }
    }

    // null si no hay sesion iniciada
    fun usuario(): Usuario? {
        if (token() == null) return null
        return Usuario(
            id = preferencias.getInt(ID, 0),
            alias = preferencias.getString(ALIAS, "").orEmpty(),
            correo = preferencias.getString(CORREO, "").orEmpty(),
            nivel = preferencias.getString(NIVEL, "").orEmpty(),
            juegoPreferido = preferencias.getString(JUEGO, "").orEmpty(),
            plataforma = preferencias.getString(PLATAFORMA, "").orEmpty()
        )
    }

    fun cerrar() {
        preferencias.edit { clear() }
    }

    private companion object {
        const val TOKEN = "token"
        const val ID = "id"
        const val ALIAS = "alias"
        const val CORREO = "correo"
        const val NIVEL = "nivel"
        const val JUEGO = "juego_preferido"
        const val PLATAFORMA = "plataforma"
    }
}