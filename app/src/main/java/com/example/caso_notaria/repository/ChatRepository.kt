package com.example.caso_notaria.repository

import com.example.caso_notaria.model.GrupoChat
import com.example.caso_notaria.model.GrupoChatDao
import com.example.caso_notaria.model.Mensaje
import com.example.caso_notaria.model.MensajeDao

// Grupos de chat de los eventos y sus mensajes
class ChatRepository(
    private val grupoChatDao: GrupoChatDao,
    private val mensajeDao: MensajeDao
) {

    // Grupos de los eventos donde el usuario esta inscrito o que organiza
    suspend fun obtenerGruposDelUsuario(usuarioId: Int): List<GrupoChat> =
        grupoChatDao.obtenerPorUsuario(usuarioId)

    suspend fun obtenerMensajes(grupoId: Int): List<Mensaje> = mensajeDao.getByGrupoId(grupoId)

    suspend fun obtenerUltimoMensaje(grupoId: Int): Mensaje? = mensajeDao.obtenerUltimo(grupoId)

    // No guarda mensajes vacios
    suspend fun enviarMensaje(grupoId: Int, autorId: Int, texto: String) {
        if (texto.isBlank()) return
        mensajeDao.insert(Mensaje(grupoId = grupoId, autorId = autorId, texto = texto.trim()))
    }
}