package com.example.caso_notaria.model

import androidx.room.*

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    suspend fun getAll(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Usuario?

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): Usuario?

    // Solo un usuario puede tener sesionActiva = 1
    @Query("UPDATE usuarios SET sesionActiva = 0")
    suspend fun cerrarTodasLasSesiones()

    @Query("UPDATE usuarios SET sesionActiva = 1 WHERE id = :id")
    suspend fun activarSesion(id: Int)

    @Query("SELECT * FROM usuarios WHERE sesionActiva = 1 LIMIT 1")
    suspend fun obtenerUsuarioActivo(): Usuario?

    // Usuarios inscritos en un evento
    @Query("SELECT usuarios.* FROM usuarios INNER JOIN inscripciones ON usuarios.id = inscripciones.usuarioId WHERE inscripciones.eventoId = :eventoId")
    suspend fun obtenerParticipantes(eventoId: Int): List<Usuario>

    // Devuelve el id del usuario nuevo
    @Insert
    suspend fun insert(usuario: Usuario): Long

    @Update
    suspend fun update(usuario: Usuario)

    @Delete
    suspend fun delete(usuario: Usuario)
}