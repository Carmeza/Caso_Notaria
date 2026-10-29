package com.example.caso_notaria.model

import androidx.room.*

@Dao
interface GrupoChatDao {
    @Query("SELECT * FROM grupos_chat")
    suspend fun getAll(): List<GrupoChat>

    @Query("SELECT * FROM grupos_chat WHERE eventoId = :eventoId")
    suspend fun getByEventoId(eventoId: Int): List<GrupoChat>

    @Insert
    suspend fun insert(grupoChat: GrupoChat)

    @Update
    suspend fun update(grupoChat: GrupoChat)

    @Delete
    suspend fun delete(grupoChat: GrupoChat)
}