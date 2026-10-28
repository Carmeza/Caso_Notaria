package com.example.caso_notaria.model

import androidx.room.*

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    suspend fun getAll(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario?

    @Insert
    suspend fun insert(usuario: Usuario)

    @Update
    suspend fun update(usuario: Usuario)

    @Delete
    suspend fun delete(usuario: Usuario)
}