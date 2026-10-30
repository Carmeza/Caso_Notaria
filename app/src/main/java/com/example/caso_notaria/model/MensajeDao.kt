package com.example.caso_notaria.model

import androidx.room.*

@Dao
interface MensajeDao {
    @Query("SELECT * FROM mensajes")
    suspend fun getAll(): List<Mensaje>

    @Query("SELECT * FROM mensajes WHERE grupoId = :grupoId ORDER BY hora ASC")
    suspend fun getByGrupoId(grupoId: Int): List<Mensaje>

    @Insert
    suspend fun insert(mensaje: Mensaje)

    @Delete
    suspend fun delete(mensaje: Mensaje)
}