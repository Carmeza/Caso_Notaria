package com.example.caso_notaria.model

import androidx.room.*

@Dao
interface EventoDao {
    @Query("SELECT * FROM eventos")
    suspend fun getAll(): List<Evento>

    @Query("SELECT * FROM eventos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Evento?

    @Insert
    suspend fun insert(evento: Evento)

    @Update
    suspend fun update(evento: Evento)

    @Delete
    suspend fun delete(evento: Evento)
}