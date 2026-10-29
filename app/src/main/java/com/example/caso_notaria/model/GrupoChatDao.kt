package com.example.caso_notaria.model

import androidx.room.*

@Dao
interface GrupoChatDao {
    @Query("SELECT * FROM grupos_chat")
    suspend fun getAll(): List<GrupoChat>

    @Query("SELECT * FROM grupos_chat WHERE eventoId = :eventoId")
    suspend fun getByEventoId(eventoId: Int): List<GrupoChat>

    // Grupos de los eventos donde el usuario esta inscrito o que organiza
    @Query(
        "SELECT grupos_chat.* FROM grupos_chat INNER JOIN inscripciones ON grupos_chat.eventoId = inscripciones.eventoId WHERE inscripciones.usuarioId = :usuarioId " +
                "UNION " +
                "SELECT grupos_chat.* FROM grupos_chat INNER JOIN eventos ON grupos_chat.eventoId = eventos.id WHERE eventos.organizadorId = :usuarioId"
    )
    suspend fun obtenerPorUsuario(usuarioId: Int): List<GrupoChat>

    @Insert
    suspend fun insert(grupoChat: GrupoChat)

    @Update
    suspend fun update(grupoChat: GrupoChat)

    @Delete
    suspend fun delete(grupoChat: GrupoChat)
}