package com.example.caso_notaria.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Clase para recibir los datos combinados de las tablas
data class ChatResultadoBD(
    val id: Int,
    val titulo: String,
    val ultimoMensaje: String?,
    val emisorUltimoMensaje: String?,
    val horaMs: Long?,
    val fechaEvento: String,
    val integrantes: Int
)

@Dao
interface GrupoChatDao {

    @Query("""
        SELECT gc.id, 
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
            SELECT MAX(id) FROM mensajes WHERE grupoId = gc.id
        )
        LEFT JOIN usuarios u ON m.autorId = u.id
        WHERE i.usuarioId = :usuarioId OR e.organizadorId = :usuarioId
        GROUP BY gc.id
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

    // AQUÍ ESTÁ LA FUNCIÓN FALTANTE QUE RESUELVE EL ERROR DE LA LÍNEA 45
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: GrupoChat): Long
}