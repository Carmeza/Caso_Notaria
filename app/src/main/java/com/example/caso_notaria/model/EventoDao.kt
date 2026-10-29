package com.example.caso_notaria.model

import androidx.room.*

@Dao
interface EventoDao {
    @Query("SELECT * FROM eventos")
    suspend fun getAll(): List<Evento>

    @Query("SELECT * FROM eventos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Evento?

    // Eventos en los que esta inscrito un usuario
    @Query("SELECT eventos.* FROM eventos INNER JOIN inscripciones ON eventos.id = inscripciones.eventoId WHERE inscripciones.usuarioId = :usuarioId")
    suspend fun obtenerInscritosPorUsuario(usuarioId: Int): List<Evento>

    @Query("SELECT * FROM eventos WHERE organizadorId = :usuarioId")
    suspend fun obtenerOrganizadosPorUsuario(usuarioId: Int): List<Evento>

    // Devuelve el id del evento nuevo
    @Insert
    suspend fun insert(evento: Evento): Long

    @Update
    suspend fun update(evento: Evento)

    @Delete
    suspend fun delete(evento: Evento)
}