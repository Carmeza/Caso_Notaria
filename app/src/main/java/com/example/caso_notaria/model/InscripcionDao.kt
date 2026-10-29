package com.example.caso_notaria.model

import androidx.room.*

@Dao
interface InscripcionDao {
    @Query("SELECT * FROM inscripciones")
    suspend fun getAll(): List<Inscripcion>

    @Query("SELECT * FROM inscripciones WHERE eventoId = :eventoId")
    suspend fun getByEventoId(eventoId: Int): List<Inscripcion>

    @Query("SELECT * FROM inscripciones WHERE usuarioId = :usuarioId")
    suspend fun getByUsuarioId(usuarioId: Int): List<Inscripcion>

    @Query("SELECT COUNT(*) FROM inscripciones WHERE eventoId = :eventoId")
    suspend fun contarPorEvento(eventoId: Int): Int

    @Query("SELECT COUNT(*) > 0 FROM inscripciones WHERE eventoId = :eventoId AND usuarioId = :usuarioId")
    suspend fun estaInscrito(eventoId: Int, usuarioId: Int): Boolean

    @Insert
    suspend fun insert(inscripcion: Inscripcion)

    @Delete
    suspend fun delete(inscripcion: Inscripcion)
}