package com.example.caso_notaria.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// Fila de la lista de chats, el grupo con su evento y su ultimo mensaje
data class ChatResultadoBD(
    val id: Int,
    val eventoId: Int,
    val titulo: String,
    val ultimoMensaje: String?,
    val emisorUltimoMensaje: String?,
    val horaMs: Long?,
    val fechaEvento: String,
    val integrantes: Int
)

@Dao
interface GrupoChatDao {

    // Grupos donde el usuario esta inscrito o que organiza, con su ultimo mensaje.
    @Query("""
        SELECT gc.id,
               e.id AS eventoId,
               e.nombre AS titulo,
               m.texto AS ultimoMensaje,
               u.alias AS emisorUltimoMensaje,
               m.hora AS horaMs,
               e.fecha AS fechaEvento,
               (SELECT COUNT(*) FROM inscripciones WHERE eventoId = e.id) + 1 AS integrantes
        FROM grupos_chat gc
        INNER JOIN eventos e ON gc.eventoId = e.id
        LEFT JOIN inscripciones i ON e.id = i.eventoId
        LEFT JOIN mensajes m ON m.id = (
            SELECT id FROM mensajes WHERE grupoId = gc.id ORDER BY hora DESC LIMIT 1
        )
        LEFT JOIN usuarios u ON m.autorId = u.id
        WHERE i.usuarioId = :usuarioId OR e.organizadorId = :usuarioId
        GROUP BY gc.id
        ORDER BY m.hora DESC
    """)
    suspend fun obtenerChatsDeUsuario(usuarioId: Int): List<ChatResultadoBD>

    @Query("""
        SELECT gc.* FROM grupos_chat gc
        INNER JOIN eventos e ON gc.eventoId = e.id
        LEFT JOIN inscripciones i ON e.id = i.eventoId
        WHERE i.usuarioId = :usuarioId OR e.organizadorId = :usuarioId
        GROUP BY gc.id
    """)
    suspend fun obtenerPorUsuario(usuarioId: Int): List<GrupoChat>

    @Query("SELECT * FROM grupos_chat WHERE eventoId = :eventoId LIMIT 1")
    suspend fun obtenerPorEvento(eventoId: Int): GrupoChat?

    @Insert
    suspend fun insert(chat: GrupoChat): Long
}